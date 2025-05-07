package com.kyrie.myportfolio.templates

import androidx.lifecycle.ViewModel
import androidx.work.WorkManager
import com.kyrie.domain.TemplateUseCase
import java.util.UUID

class TemplateViewModel(private val templateUseCase: TemplateUseCase) : ViewModel() {
    //
    suspend fun fetchPdfUrl(
        fileName: String,
        onFailureListener: (Exception) -> Unit,
        onPdfUrl: (String) -> Unit,
    ) = templateUseCase.fetchPdfUrl(fileName, onFailureListener, onPdfUrl)

    fun startDownloadPdf(
        fileNameToDownload: String,
        onFailureListener: (Exception) -> Unit,
        onWorkManagerInstance: (WorkManager, UUID) -> Unit,
        intentDownloadUrl: String = "",
    ) = templateUseCase.startDownloadPdf(
        fileNameToDownload,
        onFailureListener,
        onWorkManagerInstance,
        intentDownloadUrl,
    )

    suspend fun getTemplateList() = templateUseCase.getTemplateList()
}
