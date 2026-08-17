package com.kyrie.data.repository.template

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kyrie.data.firebaseConstants.FirebaseCollections
import com.kyrie.data.firebaseConstants.FirebaseDefaultException
import com.kyrie.data.models.TemplateInfo
import com.kyrie.data.remote.State
import com.kyrie.data.remote.SupabaseStorageClient
import com.kyrie.utility.constants.PdfDownloadStrings
import com.kyrie.utility.constants.SupabaseFolder
import com.kyrie.utility.constants.SupabaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.FileNotFoundException
import java.io.IOException
import java.util.UUID

class TemplateRepositoryImpl(
    private val context: Context,
    private val supabaseStorageClient: SupabaseStorageClient,
) : TemplateRepository {
    private var downloadWorkRequest: OneTimeWorkRequest? = null
    private val templateListRef =
        Firebase.firestore.collection(FirebaseCollections.TEMPLATES.name.lowercase())
            .document(FirebaseCollections.LIST.name.lowercase())
    private val workManager = WorkManager.getInstance(context)

    override suspend fun getTemplateList(): Flow<State<TemplateInfo?>> =
        flow {
            emit(State.loading())
            try {
                val snap = templateListRef.get().await()
                val lists = snap.toObject(TemplateInfo::class.java)
                emit(State.success(lists))
            } catch (e: FirebaseException) {
                emit(
                    State.failed(
                        e.message ?: FirebaseDefaultException.FIREBASE_DEFAULT_EXCEPTION.message,
                    ),
                )
            } catch (e: Exception) {
                emit(
                    State.failed(
                        e.message
                            ?: FirebaseDefaultException.DEFAULT_EXCEPTION.message,
                    ),
                )
            }
        }

    override suspend fun fetchPdfUrl(
        fileNameToDownload: String,
        onFailureListener: (Exception) -> Unit,
        onPdfUrl: (String) -> Unit,
    ) {
        if (fileNameToDownload.isEmpty()) {
            onFailureListener.invoke(FileNotFoundException(EMPTY_FILE_NAME))
            return
        }
        val pdfUrl = SupabaseStorage.publicUrl(SupabaseFolder.RESUMES, fileNameToDownload)
        try {
            if (supabaseStorageClient.exists(pdfUrl)) {
                onPdfUrl.invoke(pdfUrl)
            } else {
                onFailureListener.invoke(FileNotFoundException(fileNameToDownload))
            }
        } catch (e: IOException) {
            onFailureListener.invoke(e)
        }
    }

    override fun startDownloadPdf(
        fileNameToDownload: String,
        onFailureListener: (Exception) -> Unit,
        onWorkManagerInstance: (WorkManager, UUID) -> Unit,
        intentDownloadUrl: String,
    ) {
        // The file name is what the PDF is saved as, so it is required even when the
        // caller already has the URL.
        if (fileNameToDownload.isEmpty()) {
            onFailureListener.invoke(FileNotFoundException(EMPTY_FILE_NAME))
            return
        }
        // The bucket is public, so the download URL is derivable from the file name and
        // needs no round trip before the worker is queued.
        val downloadUrl =
            intentDownloadUrl.ifEmpty {
                SupabaseStorage.publicUrl(SupabaseFolder.RESUMES, fileNameToDownload)
            }
        queueWorkManagerAndDownload(
            downloadUrl,
            fileNameToDownload,
            onWorkManagerInstance,
        )
    }

    private fun queueWorkManagerAndDownload(
        url: String,
        fileName: String,
        onWorkManagerInstance: (WorkManager, UUID) -> Unit,
    ) {
        val data =
            Data.Builder()
                .putString(PdfDownloadStrings.PDF_URL_KEY.value, url)
                .putString(PdfDownloadStrings.PDF_FILE_NAME_KEY.value, fileName)
                .build()

        downloadWorkRequest =
            OneTimeWorkRequestBuilder<TemplateWorkManagerImpl>()
                .setInputData(data)
                .build()

        workManager.enqueue(downloadWorkRequest!!)
        onWorkManagerInstance.invoke(workManager, downloadWorkRequest!!.id)
    }

    override suspend fun cancelDownloadWorkRequest() {
        if (downloadWorkRequest != null) {
            workManager.cancelWorkById(downloadWorkRequest!!.id)
        }
    }

    private companion object {
        const val EMPTY_FILE_NAME = "No file name was given for the PDF"
    }
}
