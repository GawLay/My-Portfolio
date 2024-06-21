package com.kyrie.data.repository.template

import androidx.work.WorkManager
import com.kyrie.data.models.TemplateInfo
import com.kyrie.data.remote.State
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface TemplateRepository {
    suspend fun getTemplateList(): Flow<State<TemplateInfo?>>
    suspend fun fetchPdfUrl(
        fileNameToDownload: String,
        onFailureListener: (Exception) -> Unit,
        onFirebaseStoragePdfUrl: (String) -> Unit
    )

     fun startDownloadPdf(
        fileNameToDownload: String,
        onFailureListener: (Exception) -> Unit,
        onWorkManagerInstance: (WorkManager, UUID) -> Unit,
        intentDownloadUrl: String = ""
    )

    suspend fun cancelDownloadWorkRequest()
}