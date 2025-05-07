package com.kyrie.data.repository.template

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kyrie.data.firebaseConstants.FirebaseCollections
import com.kyrie.data.firebaseConstants.FirebaseDefaultException
import com.kyrie.data.models.TemplateInfo
import com.kyrie.data.remote.State
import com.kyrie.utility.constants.FirebasePDFStrings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class TemplateRepositoryImpl(private val context: Context) : TemplateRepository {
    private var downloadWorkRequest: OneTimeWorkRequest? = null
    private val templateListRef =
        Firebase.firestore.collection(FirebaseCollections.TEMPLATES.name.lowercase())
            .document(FirebaseCollections.LIST.name.lowercase())
    private val workManager = WorkManager.getInstance(context)

    override suspend fun getTemplateList(): Flow<State<TemplateInfo?>> = flow {
        emit(State.loading())
        try {
            val snap = templateListRef.get().await()
            val lists = snap.toObject(TemplateInfo::class.java)
            emit(State.success(lists))
        } catch (e: FirebaseException) {
            emit(
                State.failed(
                    e.message ?: FirebaseDefaultException.FIREBASE_DEFAULT_EXCEPTION.message
                )
            )
        } catch (e: Exception) {
            emit(
                State.failed(
                    e.message
                        ?: FirebaseDefaultException.DEFAULT_EXCEPTION.message
                )
            )
        }
    }


    override suspend fun fetchPdfUrl(
        fileNameToDownload: String,
        onFailureListener: (Exception) -> Unit,
        onFirebaseStoragePdfUrl: (String) -> Unit
    ) {
        val storage = Firebase.storage
        val storageRef = storage.reference.child(FirebaseCollections.PDF.name.lowercase())
        storageRef.listAll().addOnSuccessListener { listResult ->
            for (fileRef in listResult.items) {
                // Get the file name
                if (fileNameToDownload == fileRef.name) {
                    fileRef.downloadUrl.addOnSuccessListener {
                        val downloadUrl = it.toString()
                        onFirebaseStoragePdfUrl.invoke(downloadUrl ?: "")
                    }.addOnFailureListener {
                        onFailureListener.invoke(it)
                    }
                    break
                }
            }
        }.addOnFailureListener {
            onFailureListener.invoke(it)
        }
    }


    override fun startDownloadPdf(
        fileNameToDownload: String,
        onFailureListener: (Exception) -> Unit,
        onWorkManagerInstance: (WorkManager, UUID) -> Unit,
        intentDownloadUrl: String
    ) {
        if (intentDownloadUrl.isNotEmpty()) {
            queueWorkManagerAndDownload(
                intentDownloadUrl,
                fileNameToDownload,
                onWorkManagerInstance
            )
        } else {
            val storage = Firebase.storage
            val storageRef = storage.reference.child(FirebaseCollections.PDF.name.lowercase())
            storageRef.listAll().addOnSuccessListener { listResult ->
                for (fileRef in listResult.items) {
                    // Get the file name
                    if (fileNameToDownload == fileRef.name) {
                        fileRef.downloadUrl.addOnSuccessListener {
                            val downloadUrl = it.toString()
                            queueWorkManagerAndDownload(
                                downloadUrl,
                                fileNameToDownload,
                                onWorkManagerInstance
                            )
                        }.addOnFailureListener {
                            onFailureListener.invoke(it)
                        }
                        break
                    }
                }
            }.addOnFailureListener {
                onFailureListener.invoke(it)
            }
        }
    }


    private fun queueWorkManagerAndDownload(
        url: String,
        fileName: String,
        onWorkManagerInstance: (WorkManager, UUID) -> Unit
    ) {
        val data = Data.Builder()
            .putString(FirebasePDFStrings.PDF_URL_KEY.value, url)
            .putString(FirebasePDFStrings.PDF_FILE_NAME_KEY.value, fileName)
            .build()

        downloadWorkRequest = OneTimeWorkRequestBuilder<TemplateWorkManagerImpl>()
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

}