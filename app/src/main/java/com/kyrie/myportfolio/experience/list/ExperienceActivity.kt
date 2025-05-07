package com.kyrie.myportfolio.experience.list

import android.content.Intent
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.kyrie.data.remote.State
import com.kyrie.myportfolio.base.BaseRevealActivity
import com.kyrie.myportfolio.databinding.ActivityExperienceBinding
import com.kyrie.myportfolio.experience.detail.ExperienceDetailActivity
import com.kyrie.myportfolio.shared.ExperienceListAdapter
import com.kyrie.myportfolio.shared.SharedViewModel
import com.kyrie.utility.constants.ExpIntentKeys
import com.kyrie.utility.constants.FancyToastTypes
import com.kyrie.utility.utility.ItemOffsetDecoration
import com.kyrie.utility.utility.makeSharedSceneTransitionWithDataResult
import com.kyrie.utility.utility.showFancyToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.kyrie.utility.R as UtilityR

class ExperienceActivity : BaseRevealActivity<ActivityExperienceBinding>() {
    private val viewModel: SharedViewModel by viewModel()
    private var expAdapter: ExperienceListAdapter? = null

    private val requestCode = 2000

    override fun onCreated(savedInstanceState: Bundle?) {
        changeStatusColorFromSecondaryToDefault(750L)
        setWindowAnimation()
        binding.includeToolbar.toolbarGeneric.title = getString(UtilityR.string.exp_title)
        binding.includeRcExp.rcExperience.alpha = 1f
        binding.includeShimmer.root.alpha = 1f
        getExpList()
        setRc()
    }

    override fun onActivityReenter(
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityReenter(resultCode, data)
        binding.includeRcExp.rcExperience.layoutAnimation = recyclerFadeScaleIn
    }

    private fun setWindowAnimation() {
        // screen.
//        window.exitTransition = transitionTogether {
//            interpolator = AccelerateDecelerateInterpolator()
//            this += Explode().apply {
//                mode = Explode.MODE_OUT
//            }
//        }

//        // This is the transition to be used for non-shared elements when we are return back from
//        // the detail screen.
//        window. reenterTransition = transitionTogether {
//            duration = LARGE_COLLAPSE_DURATION / 2
//            interpolator = LINEAR_OUT_SLOW_IN
//            // The app bar.
//            this += Slide(Gravity.TOP).apply {
//                mode = aSlide.MODE_IN
//                addTarget(R.id.app_bar)
//            }
//            // The grid items.
//            this += Explode().apply {
//                // The grid items should start imploding after the app bar is in.
//                startDelay = LARGE_COLLAPSE_DURATION / 2
//                mode = Explode.MODE_IN
//                excludeTarget(R.id.app_bar, true)
//            }
//        }
    }

    override fun onClickEvents() {
        binding.includeToolbar.toolbarGeneric.setNavigationOnClickListener {
            handleBackPress()
        }
    }

    private fun onItemClick(
        documentId: String,
        adapterPosition: Int,
        sharedView: View,
    ) {
        if (documentId.isEmpty()) {
            showFancyToast(getString(UtilityR.string.document_id_empty))
            return
        }
        binding.includeRcExp.rcExperience.let {
            it.layoutAnimation = recyclerFadeScaleOut
            makeSharedSceneTransitionWithDataResult<ExperienceDetailActivity>(
                requestCode,
                Pair.create(sharedView, sharedView.transitionName),
            ) {
                putExtra(ExpIntentKeys.DOCUMENT_ID.key, documentId)
                putExtra(ExpIntentKeys.ADAPTER_POS.key, adapterPosition)
            }
        }
    }

    private fun setRc() {
        expAdapter = ExperienceListAdapter(::onItemClick)
        binding.includeRcExp.rcExperience.apply {
            layoutManager = LinearLayoutManager(this@ExperienceActivity)
            adapter = expAdapter
            addItemDecoration(ItemOffsetDecoration(resources.getInteger(UtilityR.integer.item_off_set)))
        }
    }

    private fun getExpList() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getExpList().collectLatest { state ->
                    when (state) {
                        is State.Loading -> {
                            binding.includeShimmer.root.startShimmer()
                        }

                        is State.Failed -> {
                            showFancyToast(state.message, FancyToastTypes.ERROR.value)
                        }

                        is State.Success -> {
                            hideShimmerAndBindList(binding.includeShimmer.root) {
                                val data = state.data
                                if (data != null) {
                                    binding.includeRcExp.rcExperience.layoutAnimation =
                                        recyclerViewAnimation
                                    val expList = data.data
                                    if (!expList.isNullOrEmpty()) {
                                        val sortedList =
                                            expList.sortedBy {
                                                it.priority
                                            }
                                        expAdapter?.submitList(sortedList)
                                    } else {
                                        showFancyToast(
                                            "Empty Views Will be implemented Later",
                                            type = FancyToastTypes.ERROR.value,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun setBinding(inflater: LayoutInflater) = ActivityExperienceBinding.inflate(inflater)

    override fun handleBackPress() {
        lifecycleScope.launch {
            delay(200)
            changeStatusColorFromDefaultToSecondary(300L)
            revealAnimation?.unRevealActivity()
        }
    }
}
