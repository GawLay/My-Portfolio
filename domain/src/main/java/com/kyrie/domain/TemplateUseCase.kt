package com.kyrie.domain

import androidx.work.WorkManager
import com.kyrie.data.repository.template.TemplateRepository
import java.util.UUID

class TemplateUseCase(private val templateRepository: TemplateRepository) {
    suspend fun fetchPdfUrl(
        fileNameToDownload: String,
        onFailureListener: (Exception) -> Unit,
        onPdfUrl: (String) -> Unit,
    ) {
        templateRepository.fetchPdfUrl(fileNameToDownload, onFailureListener, onPdfUrl)
    }

    fun startDownloadPdf(
        fileNameToDownload: String,
        onFailureListener: (Exception) -> Unit,
        onWorkManagerInstance: (WorkManager, UUID) -> Unit,
        intentDownloadUrl: String = "",
    ) {
        templateRepository.startDownloadPdf(
            fileNameToDownload,
            onFailureListener,
            onWorkManagerInstance,
            intentDownloadUrl,
        )
    }

    suspend fun getTemplateList() = templateRepository.getTemplateList()
}
