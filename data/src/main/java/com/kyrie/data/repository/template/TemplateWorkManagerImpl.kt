package com.kyrie.data.repository.template

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.storage.FirebaseStorage
import com.kyrie.utility.constants.FirebasePDFStrings
import com.kyrie.utility.constants.NotificationChannelName
import com.kyrie.utility.constants.NotificationNotifyId
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class TemplateWorkManagerImpl(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters), KoinComponent {

    override suspend fun doWork(): Result {
        val url = inputData.getString(FirebasePDFStrings.PDF_URL_KEY.value)
            ?: return Result.failure()
        val fileNameWithExtension = inputData.getString(FirebasePDFStrings.PDF_FILE_NAME_KEY.value)
            ?: return Result.failure()
        createNotificationChannel(context = context)

        return try {
            val uri = downloadPDFFileFromFirebase(url, fileNameWithExtension)
            showDownloadCompleteNotification(context, uri)
            Result.success()
        } catch (e: IOException) {
            e.printStackTrace()
            Result.failure()
        }

    }

    private suspend fun downloadPDFFileFromFirebase(
        url: String,
        fileNameWithExtension: String
    ): Uri? {
        showProgressNotification(context)
        val firebaseStorage = FirebaseStorage.getInstance()
        val storageReference = firebaseStorage.getReferenceFromUrl(url)
        val bytes = storageReference.getBytes(Long.MAX_VALUE).await()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            savePdfUsingMediaStore(fileNameWithExtension, bytes)
        } else {
            savePdfToExternalStorage(fileNameWithExtension, bytes)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun savePdfUsingMediaStore(fileName: String, data: ByteArray): Uri? {
        val resolver = applicationContext.contentResolver

        // Check if a file with the same name already exists
        val existingUri = resolver.query(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.MediaColumns._ID),
            "${MediaStore.MediaColumns.DISPLAY_NAME} = ?",
            arrayOf(fileName),
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id)
            } else {
                null
            }
        }

        // If the file exists, delete it
        existingUri?.let {
            resolver.delete(it, null, null)
        }

        // Create a new file and write the data
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let { newUri ->
            resolver.openFileDescriptor(newUri, "w")?.use { descriptor ->
                FileOutputStream(descriptor.fileDescriptor).use { outputStream ->
                    outputStream.write(data)
                }
            }
        }
        return uri


    }

    private fun savePdfToExternalStorage(fileName: String, data: ByteArray): Uri? {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        val pdfFile = File(downloadsDir, fileName)
        if (pdfFile.exists()) {
            pdfFile.delete() // Delete the existing file
        }
        pdfFile.writeBytes(data)
        return Uri.fromFile(pdfFile)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = NotificationChannelName.PDF_DOWNLOAD_CHANNEL.value
            val descriptionText = NotificationChannelName.PDF_DOWNLOAD_CHANNEL_DESC.value
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NotificationChannelName.PDF_DOWNLOAD_CHANNEL_ID.value, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    fun showProgressNotification(context: Context, progress: Int = 0) {
        val builder = NotificationCompat.Builder(context, NotificationChannelName.PDF_DOWNLOAD_CHANNEL_ID.value)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("Downloading File")
            .setContentText("Download in progress")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOnlyAlertOnce(true)
//            .setProgress(100, progress, false)

        NotificationManagerCompat.from(context).notify(NotificationNotifyId.PDF_NOTIFICATION_NOTIFY_ID.id, builder.build())
    }

    @SuppressLint("MissingPermission")
    fun showDownloadCompleteNotification(context: Context, uri: Uri?) {
        // Create an Intent to open the PDF file with an action to view
        // Share the content URI with another app
        val shareIntent = Intent().apply {
            action = Intent.ACTION_VIEW
            setDataAndType(uri,"application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant read permission to the receiving app
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(context, NotificationChannelName.PDF_DOWNLOAD_CHANNEL_ID.value)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("Download Complete")
            .setContentText("My awesome resume file has landed on your phone.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(NotificationNotifyId.PDF_NOTIFICATION_NOTIFY_ID.id, builder.build())
    }

    fun getPathFromUri(context: Context, uri: Uri): String? {
        var filePath: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                filePath = cursor.getString(columnIndex)
            }
        }
        return filePath
    }

}