package com.kyrie.myportfolio.aboutMe

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kyrie.data.models.Profile
import com.kyrie.data.remote.State
import com.kyrie.myportfolio.R
import com.kyrie.myportfolio.base.BaseRevealActivity
import com.kyrie.myportfolio.databinding.ActivityAboutMeBinding
import com.kyrie.myportfolio.experience.list.ExperienceActivity
import com.kyrie.myportfolio.myResume.MyResumeActivity
import com.kyrie.myportfolio.setting.SettingActivity
import com.kyrie.myportfolio.skill.SkillActivity
import com.kyrie.myportfolio.templates.TemplateActivity
import com.kyrie.utility.animation.ALPHA
import com.kyrie.utility.animation.CircularRevealAnim
import com.kyrie.utility.animation.createAnim
import com.kyrie.utility.constants.BundleKeys
import com.kyrie.utility.constants.FancyToastTypes
import com.kyrie.utility.constants.SharedElementsNames
import com.kyrie.utility.math.GetXY
import com.kyrie.utility.utility.callPhone
import com.kyrie.utility.utility.changeStatusBarColor
import com.kyrie.utility.utility.getByteArrayFromImageView
import com.kyrie.utility.utility.loadUrl
import com.kyrie.utility.utility.makeSharedSceneTransitionWithData
import com.kyrie.utility.utility.overridePendingTransitionExt
import com.kyrie.utility.utility.setHtmlText
import com.kyrie.utility.utility.setSafeOnClickListener
import com.kyrie.utility.utility.showFancyToast
import com.kyrie.utility.utility.startIntent
import com.kyrie.utility.utility.startRevealIntent
import com.kyrie.utility.utility.toggleItemClick
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.util.Pair as UtilPair

class AboutMeActivity : BaseRevealActivity<ActivityAboutMeBinding>() {
    private val viewModel: AboutMeViewModel by viewModel()
    private var myPhoneNumber = ""

    private val requestCode = 1000

    override fun onCreated(savedInstanceState: Bundle?) {
        changeStatusBarColor(secondaryStatusColor)
        binding.btmNavAboutMe.toggleItemClick(false)
        binding.fabMore.hide()
        setSharedElementNames()
        getProfileData()
    }

    //    region Data SetUp ####################
    private fun getProfileData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getProfile().collectLatest { state ->
                    when (state) {
                        is State.Loading -> {
                            binding.includeShimmer.root.startShimmer()
                        }

                        is State.Failed -> {
                            showFancyToast(state.message, FancyToastTypes.ERROR.value)
                        }

                        is State.Success -> {
                            hideShimmerAndSetupViews(state.data)
                        }
                    }
                }
            }
        }
    }

    private fun hideShimmerAndSetupViews(data: Profile?) {
        binding.includeShimmer.root.let {
            it.createAnim(ALPHA, 1f, 0f, shimmerAnimDuration).apply {
                it.stopShimmer()
                it.visibility = View.GONE
                if (data != null) {
                    setProfileInfo(data)
                    setDescriptionWithBottomAction(data.description_one, data.phone)
                }
            }
        }
    }

    private fun setProfileInfo(data: Profile) {
        binding.ivProfile.loadUrl(data.image_url ?: "")
        binding.tvName.apply {
            text = data.name
            visibility = View.VISIBLE
        }
        binding.tvJobTitle.apply {
            text = data.job_title
            visibility = View.VISIBLE
        }
        binding.tvGmail.apply {
            text = data.gmail
            visibility = View.VISIBLE
        }
        binding.tvPreviewTemplate.apply {
            setHtmlText(data.template_preview ?: "")
            visibility = View.VISIBLE
        }
    }

    private fun setDescriptionWithBottomAction(
        descriptionOne: String?,
        phone: String?,
    ) {
        binding.tvDescription.let {
            val defaultDesc = getString(com.kyrie.utility.R.string.about_me)
            it.text = descriptionOne ?: defaultDesc
            it.visibility = View.VISIBLE
        }
        binding.includeResume.root.visibility = View.VISIBLE
        if (!phone.isNullOrEmpty()) {
            myPhoneNumber = phone
            binding.includeCallMe.root.visibility = View.VISIBLE
        }
        binding.fabMore.show()
    }
//    endregion Data SetUp ####################

    //    region View Utility #####################
    private fun setSharedElementNames() {
        binding.clHeader.transitionName = SharedElementsNames.IV_PROFILE_SHARED.name
    }

    override fun onClickEvents() {
        binding.fabMore.setSafeOnClickListener {
            binding.fabMore.isExpanded = true
            lifecycleScope.launch {
                delay(300)
                binding.btmNavAboutMe.toggleItemClick(true)
            }
        }

        binding.btmNavAboutMe.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_skill -> {
                    binding.fabMore.isExpanded = false
                    startRevealActivity<SkillActivity>()
                }

                R.id.menu_about -> {
                    binding.fabMore.isExpanded = false
                    startRevealActivity<SettingActivity>()
                }

                R.id.menu_experience -> {
                    binding.fabMore.isExpanded = false
                    startRevealActivity<ExperienceActivity>()
                }

                R.id.menu_return -> {
                    binding.btmNavAboutMe.toggleItemClick(false)
                    binding.fabMore.isExpanded = false
                }
            }
            true
        }

        binding.includeCallMe.root.setSafeOnClickListener {
            lifecycleScope.launch {
                delay(200)
                callPhone(myPhoneNumber)
            }
        }

        binding.includeResume.root.setSafeOnClickListener {
            binding.tvGmail.apply {
                with(gmailFadeInAndOut(fromReEnter = false)) {
                    duration = 200L
                    changeStatusColorFromSecondaryToDefault(400L)
                    doOnEnd {
                        makeSharedSceneTransitionWithData<MyResumeActivity>(
                            requestCode,
                            UtilPair.create(binding.clHeader, binding.clHeader.transitionName),
                        ) {
                            putExtra(
                                BundleKeys.ABOUT_ME_BITMAP_KEY.key,
                                binding.ivProfile.getByteArrayFromImageView(),
                            )
                        }
                    }
                    start()
                }
            }
        }
        binding.tvPreviewTemplate.setSafeOnClickListener {
            startIntent<TemplateActivity>()
            overridePendingTransitionExt(
                false,
                com.kyrie.utility.R.anim.item_animation_slide_from_bottom,
                com.kyrie.utility.R.anim.anim_stay_still,
            )
        }
    }

    private inline fun <reified T> startRevealActivity() {
        lifecycleScope.launch {
//            changeStatusColorFromSurfaceToGrey(1000L)
            delay(500)
            val fabCx = GetXY.getCenterValueOfViewOnWindow(binding.fabMore)[0] + 60
            val fabCy = GetXY.getCenterValueOfViewOnWindow(binding.fabMore)[1] + 60
            startRevealIntent<T> {
                putExtra(CircularRevealAnim.EXTRA_CIRCULAR_REVEAL_X, fabCx)
                putExtra(CircularRevealAnim.EXTRA_CIRCULAR_REVEAL_Y, fabCy)
            }
            overridePendingTransitionExt(
                false,
                com.kyrie.utility.R.anim.anim_stay_still,
                com.kyrie.utility.R.anim.item_animation_slide_from_bottom,
            )
            // prevent default transition animation
        }
    }

    override fun onActivityReenter(
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityReenter(resultCode, data)
        // need to set back to surface since we change status  bar to white before transition to next page
        changeStatusBarColor(secondaryStatusColor)
        binding.tvGmail.apply {
            lifecycleScope.launch {
                delay(500)
                with(gmailFadeInAndOut(fromReEnter = true)) {
                    duration = 500L
                    start()
                }
            }
        }
    }

    private fun TextView.gmailFadeInAndOut(fromReEnter: Boolean): ObjectAnimator {
        val startValue: Float
        val endValue: Float
        if (fromReEnter) {
            startValue = 0f
            endValue = 1f
        } else {
            startValue = 1f
            endValue = 0f
        }
        return if (fromReEnter) {
            createAnim(ALPHA, startValue, endValue)
        } else {
            createAnim(ALPHA, startValue, endValue)
        }
    }
//    endregion View Utility #####################

    override fun setBinding(inflater: LayoutInflater) = ActivityAboutMeBinding.inflate(inflater)

    override fun handleBackPress() {
        finishAffinity()
    }
}
