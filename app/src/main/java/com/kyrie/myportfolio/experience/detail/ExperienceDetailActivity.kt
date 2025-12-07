package com.kyrie.myportfolio.experience.detail

import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kyrie.data.models.ExperienceDetail
import com.kyrie.data.remote.State
import com.kyrie.myportfolio.base.BaseActivity
import com.kyrie.myportfolio.databinding.ActivityExperienceDetailBinding
import com.kyrie.myportfolio.experience.detail.vpAdapter.ExpDetailVPAdapter
import com.kyrie.utility.animation.ALPHA
import com.kyrie.utility.animation.TransitionUtils
import com.kyrie.utility.animation.createAnim
import com.kyrie.utility.animation.getAnimSet
import com.kyrie.utility.constants.ExpIntentKeys
import com.kyrie.utility.constants.SharedElementsNames
import com.kyrie.utility.simplifyInteractors.AnimatorListenerAdapter
import com.kyrie.utility.utility.awaitViewDraw
import com.kyrie.utility.utility.showFancyToast
import com.kyrie.utility.utility.showLog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.kyrie.utility.R as UtilityR

class ExperienceDetailActivity : BaseActivity<ActivityExperienceDetailBinding>() {
    private val viewModel: ExperienceDetailViewModel by viewModel()

    private val tabTitles by lazy {
        arrayOf(UtilityR.string.tab_detail_title, UtilityR.string.tab_info_title)
    }

    private val documentID: String? by lazy {
        intent.getStringExtra(ExpIntentKeys.DOCUMENT_ID.key)
    }
    private val sharedPosition: Int by lazy {
        intent.getIntExtra(ExpIntentKeys.ADAPTER_POS.key, 0)
    }

    override fun onCreated(savedInstanceState: Bundle?) {
        setupStatusBar()
        postponeEnterTransition()
        awaitViewDraw {
            startPostponedEnterTransition()
        }
//        hideStatusBar()
        setSharedElementNames()
        setWindowTransition()
        setSharedTransitions()
//        setRc()
        setVp()
        getDetails()
    }

    private fun setupStatusBar() {
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).apply {
            // Set to true for dark icons on light background (colorSecondary)
            // Set to false for light icons on dark background
            isAppearanceLightStatusBars = true
        }
    }

    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()

    private fun setVp() {
        val vpAdapter = ExpDetailVPAdapter(supportFragmentManager, lifecycle)
        binding.vpExpDetail.adapter = vpAdapter
//        val mediator =
//            TabLayoutMediator(binding.tabExpDetail, binding.vpExpDetail) { tab, position ->
//                tab.text = getString(tabTitles[position]) }
//        mediator.attach()
    }

    override fun onClickEvents() {
        binding.imgBtnBack.setOnClickListener {
            handleBackPress()
        }
    }

    private fun setWindowTransition() {
        window.enterTransition =
            TransitionUtils.getExpDetailWindowTransition(
                this,
                binding.viewHeaderBg, binding.vpExpDetail,
            )
        window.exitTransition =
            TransitionUtils.getExpDetailWindowTransition(
                this,
                binding.viewHeaderBg, binding.vpExpDetail,
            )
    }

    private fun setSharedElementNames() {
        showLog("Shared Position $sharedPosition")
        binding.includeJobCard.root.transitionName =
            SharedElementsNames.JOB_CARD_SHARED.name + sharedPosition
    }

    private fun setSharedTransitions() {
        window.sharedElementEnterTransition = TransitionUtils.createExpDetailSharedEnterTransition()
    }

    private fun getDetails() {
        // bla
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getExpDetail(documentID ?: "").collectLatest {
                    when (it) {
                        is State.Loading -> {
//                            showFancyToast("Loading")
                        }

                        is State.Failed -> {
                            showFancyToast("Error ${it.message}")
                        }

                        is State.Success -> {
                            val data = it.data
                            if (data != null) {
                                val expDetail = data.detail
                                if (expDetail != null) {
                                    setJobCard(expDetail)
                                    viewModel.setExperienceDetailsList(expDetail)
                                } else {
                                    showFancyToast("Empty")
                                }
                            } else {
                                showFancyToast("Empty")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setJobCard(data: ExperienceDetail) {
        binding.includeJobCard.apply {
            data.let {
                tvCompanyName.text = it.jobTitle
                tvPosition.text = it.position
                tvLocation.text = it.location
                tvTimestamp.text = it.timestamp
                tvJobType.text = it.jobType
            }
        }
    }

    override fun setBinding(inflater: LayoutInflater) = ActivityExperienceDetailBinding.inflate(inflater)

    private fun hideVP() {
        // now we have to hide view pager
//        val expFadeOut = binding.tabExpDetail.createAnim(ALPHA, 1f, 0f, 300)
        val skillFadeOut = binding.vpExpDetail.createAnim(ALPHA, 1f, 0f, 300)
        val aniSet = getAnimSet(skillFadeOut)
        aniSet.addListener(
            AnimatorListenerAdapter(
                onStart = {
                    binding.vpExpDetail.layoutAnimation = recyclerViewExitAnimation
                },
            ),
        )
        aniSet.start()
    }

    override fun handleBackPress() {
        hideVP()
        setResult(RESULT_OK)
        finishAfterTransition()
    }
}
