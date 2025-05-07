package com.kyrie.myportfolio.templates

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.material.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kyrie.data.remote.State
import com.kyrie.myportfolio.base.BaseActivity
import com.kyrie.myportfolio.databinding.ActivityTemplateBinding
import com.kyrie.myportfolio.templates.pdf.TemplateViewerActivity
import com.kyrie.utility.animation.ALPHA
import com.kyrie.utility.animation.HALF_SECOND
import com.kyrie.utility.animation.SCALE_X
import com.kyrie.utility.animation.SCALE_Y
import com.kyrie.utility.animation.createAnim
import com.kyrie.utility.animation.startAnimSet
import com.kyrie.utility.constants.FancyToastTypes
import com.kyrie.utility.constants.TemplateIntentKey
import com.kyrie.utility.utility.RecyclerMarginHorizontalHelper
import com.kyrie.utility.utility.overridePendingTransitionExt
import com.kyrie.utility.utility.showFancyToast
import com.kyrie.utility.utility.startIntentWithData
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.UUID
import kotlin.math.abs
import com.kyrie.utility.R as UtilityR

class TemplateActivity : BaseActivity<ActivityTemplateBinding>() {
    private lateinit var fileName: String
    private val templateViewModel: TemplateViewModel by inject()
    private lateinit var templateAdapter: TemplateAdapter

    private var hasNotificationPermissionGranted = false

    private val marginDistanceBetweenPage by lazy {
        resources.getDimensionPixelSize(UtilityR.dimen.template_margin_distance)
    }
    private val paddingSizeForCard by lazy {
        resources.getDimensionPixelSize(UtilityR.dimen.template_edge_distance)
    }

    override fun onCreated(savedInstanceState: Bundle?) {
        setPager()
        observe()
    }

    private fun observe() {
        lifecycleScope.launch {
            templateViewModel.getTemplateList().collectLatest { state ->
                when (state) {
                    is State.Loading -> {
//                            showFancyToast(
//                                "Loading Views Will be implemented Later",
//                                type = FancyToastTypes.CONFUSING.value
//                            )
                    }

                    is State.Failed -> {
                        //
                    }

                    is State.Success -> {
                        setTemplateTitle(state.data?.template_title)
                        templateAdapter.submitList(state.data?.data)
                    }
                }
            }
        }
    }

    private fun setTemplateTitle(templateTitle: String?) {
        binding.tvTemplateTitle.apply {
            text = templateTitle
            val fadeIn = createAnim(ALPHA, 0f, 1f, HALF_SECOND)
            val scaleX = createAnim(SCALE_X, 0.5f, 1f, HALF_SECOND)
            val scaleY = createAnim(SCALE_Y, 0.5f, 1f, HALF_SECOND)
            startAnimSet(
                fadeIn,
                scaleX,
                scaleY,
            )
        }
    }

    private fun setPager() {
        templateAdapter = TemplateAdapter(::onTemplateItemClick, ::onDownloadClick)
        binding.vpTemplate.apply {
            adapter = templateAdapter
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
            addItemDecoration(RecyclerMarginHorizontalHelper(marginDistanceBetweenPage))
            setPadding(paddingSizeForCard, 0, paddingSizeForCard, 0)

            setPageTransformer { page, position ->
                val scale = 0.90f + (1 - abs(position)) * 0.10f
                // to display the page in the middle of the screen larger
                page.scaleY = scale
            }
        }
    }

    private fun fetchPdfUrlAndDisplay() {
        lifecycleScope.launch {
            templateViewModel.fetchPdfUrl(
                this@TemplateActivity.fileName,
                ::onFailedDownloadUrl,
                onPdfUrl = {
                    startIntentWithData<TemplateViewerActivity> {
                        putExtra(TemplateIntentKey.PDF_FILE_NAME.key, fileName)
                        putExtra(TemplateIntentKey.PDF_PREVIEW_URL.key, it)
                    }
                },
            )
        }
    }

    private fun observeWorkManager(
        workManager: WorkManager,
        uuid: UUID,
    ) {
        workManager.getWorkInfoByIdLiveData(uuid)
            .observe(this) {
                if (it?.state == WorkInfo.State.FAILED) {
                    showFancyToast("Failed to downloaded")
                }
                if (it?.state == WorkInfo.State.SUCCEEDED) {
                    showFancyToast("Successfully downloaded")
                }
            }
    }

    private fun onTemplateItemClick(fileName: String) {
        this.fileName = fileName
        fetchPdfUrlAndDisplay()
    }

    private fun onDownloadClick(fileName: String) {
        this.fileName = fileName
        if (Build.VERSION.SDK_INT >= 33) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            templateViewModel.startDownloadPdf(
                fileName,
                ::onFailedDownloadUrl,
                ::observeWorkManager,
            )
        }
    }

    private fun onFailedDownloadUrl(exception: Exception) {
        showFancyToast("Failure ${exception.message}", FancyToastTypes.ERROR.value)
    }

    override fun setBinding(inflater: LayoutInflater) = ActivityTemplateBinding.inflate(inflater)

    override fun handleBackPress() {
        finish()
        overridePendingTransitionExt(
            true,
            UtilityR.anim.anim_stay_still,
            UtilityR.anim.item_animation_slide_from_top,
        )
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasNotificationPermissionGranted = isGranted
            if (!isGranted) {
                if (Build.VERSION.SDK_INT >= 33) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                        showNotificationPermissionRationale()
                    } else {
                        showSettingDialog()
                    }
                }
            } else {
                templateViewModel.startDownloadPdf(
                    fileName,
                    ::onFailedDownloadUrl,
                    ::observeWorkManager,
                )
            }
        }

    private fun showSettingDialog() {
        MaterialAlertDialogBuilder(
            this,
            R.style.MaterialAlertDialog_Material3,
        )
            .setTitle("Notification Permission")
            .setMessage(
                "You will no longer see notification about PDF downloading status. " +
                    "Please enable manually if you want to see again.",
            )
            .setPositiveButton("Ok") { _, _ -> }
            .show()
    }

    private fun showNotificationPermissionRationale() {
        MaterialAlertDialogBuilder(
            this,
            R.style.MaterialAlertDialog_Material3,
        )
            .setTitle("Alert")
            .setMessage("Notification permission is required, to show notification")
            .setPositiveButton("Ok") { _, _ ->
                if (Build.VERSION.SDK_INT >= 33) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
