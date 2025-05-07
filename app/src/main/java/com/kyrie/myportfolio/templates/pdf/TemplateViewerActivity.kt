package com.kyrie.myportfolio.templates.pdf

import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.kyrie.domain.TemplateUseCase
import com.kyrie.myportfolio.base.BaseActivity
import com.kyrie.myportfolio.databinding.ActivityTemplatePdfViewerBinding
import com.kyrie.utility.constants.FancyToastTypes
import com.kyrie.utility.constants.TemplateIntentKey
import com.kyrie.utility.utility.setSafeOnClickListener
import com.kyrie.utility.utility.showFancyToast
import com.kyrie.utility.utility.showLog
import org.koin.android.ext.android.inject
import java.util.UUID

class TemplateViewerActivity : BaseActivity<ActivityTemplatePdfViewerBinding>() {
    private val templateUseCase: TemplateUseCase by inject()
    private val firebaseUrl: String by lazy {
        intent.getStringExtra(TemplateIntentKey.PDF_PREVIEW_URL.key) ?: ""
    }
    private val fileName: String by lazy {
        intent.getStringExtra(TemplateIntentKey.PDF_FILE_NAME.key) ?: ""
    }

    override fun onCreated(savedInstanceState: Bundle?) {
        binding.includeTemplateDownload.imgBtnDownload.setSafeOnClickListener {
            templateUseCase.startDownloadPdf(
                fileName,
                ::onFailedDownloadUrl,
                ::observeWorkManager,
                intentDownloadUrl = firebaseUrl,
            )
        }
        binding.pdfRendererViewTemplate
            .initWithUrl(
                firebaseUrl,
                lifecycleCoroutineScope = lifecycleScope,
                lifecycle = lifecycle,
            )
    }

    private fun observeWorkManager(
        workManager: WorkManager,
        uuid: UUID,
    ) {
        workManager.getWorkInfoByIdLiveData(uuid)
            .observe(this) {
                if (it?.state == WorkInfo.State.FAILED) {
                    showLog("Failed")
                }
                if (it?.state == WorkInfo.State.SUCCEEDED) {
                    showLog("SUCCEED ")
                }
            }
    }

    private fun onFailedDownloadUrl(exception: Exception) {
        showFancyToast("Failure ${exception.message}", FancyToastTypes.ERROR.value)
    }

    override fun setBinding(inflater: LayoutInflater) = ActivityTemplatePdfViewerBinding.inflate(inflater)

    override fun handleBackPress() {
        finish()
    }
}
