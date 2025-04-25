package com.kyrie.myportfolio.myResume

import android.animation.AnimatorSet
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.kyrie.data.models.Experiences
import com.kyrie.data.models.Profile
import com.kyrie.data.remote.State
import com.kyrie.myportfolio.base.BaseActivity
import com.kyrie.myportfolio.databinding.ActivityResumeBinding
import com.kyrie.myportfolio.experience.detail.ExperienceDetailActivity
import com.kyrie.myportfolio.shared.ExperienceListAdapter
import com.kyrie.myportfolio.shared.SharedViewModel
import com.kyrie.myportfolio.webView.WebViewActivity
import com.kyrie.utility.animation.ALPHA
import com.kyrie.utility.animation.HALF_SECOND
import com.kyrie.utility.animation.SCALE_X
import com.kyrie.utility.animation.SCALE_Y
import com.kyrie.utility.animation.TransitionUtils
import com.kyrie.utility.animation.createAnim
import com.kyrie.utility.animation.getAnimSet
import com.kyrie.utility.animation.startAnimSet
import com.kyrie.utility.constants.BundleKeys
import com.kyrie.utility.constants.ExpIntentKeys
import com.kyrie.utility.constants.FancyToastTypes
import com.kyrie.utility.constants.SharedElementsNames
import com.kyrie.utility.constants.WebViewIntentKey
import com.kyrie.utility.simplifyInteractors.AnimatorListenerAdapter
import com.kyrie.utility.utility.ItemOffsetDecoration
import com.kyrie.utility.utility.awaitViewDraw
import com.kyrie.utility.utility.getBitmap
import com.kyrie.utility.utility.makeSharedSceneTransitionWithDataResult
import com.kyrie.utility.utility.setMarkdown
import com.kyrie.utility.utility.showFancyToast
import com.kyrie.utility.utility.startGmail
import com.kyrie.utility.utility.startIntentWithData
import com.kyrie.utility.utility.startLinkedIn
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.kyrie.utility.R as UtilityR


class MyResumeActivity : BaseActivity<ActivityResumeBinding>() {
    private val viewModel: SharedViewModel by viewModel()
    private var aniSet: AnimatorSet? = null
    private var expAdapter: ExperienceListAdapter? = null
    private var isBackVisible = false
    private var linkedinUrl: String? = null
    private var githubUrl: String? = null

    companion object {
        const val GMAIL = ":GMAIL"
        const val GIT = ":GIT"
        const val LINKEDIN = ":LINKEDIN"
    }

    @Suppress("PrivatePropertyName")
    private val REQUEST_CODE = 2000

    private var urlType = ""


    override fun onCreated(savedInstanceState: Bundle?) {
        val byteArray = intent.getByteArrayExtra(BundleKeys.ABOUT_ME_BITMAP_KEY.key)
        if (byteArray != null) {
            binding.includeResumeHeader.ivProfile.setImageBitmap(byteArray.getBitmap())
        }
        postponeEnterTransition()
        setSharedElementNames()
        setWindowTransitions()
        setSharedTransitions()
        awaitViewDraw()
        showViews()
        setRc()
        getProfile()
        getExpList()
    }


    private fun getProfile() {
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
                            val data = state.data
                            linkedinUrl = data?.linkedin_url
                            githubUrl = data?.github_url
                            setJobDescription(data)
                        }

                    }
                }
            }
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
                            hideShimmerAndBindList(state.data)
                        }

                    }
                }
            }
        }
    }

    override fun onClickEvents() {
        super.onClickEvents()
        binding.includeResumeHeader.apply {

            btnBack.setOnClickListener {
                finishActivity()
            }
            includeGmail.root.setOnClickListener {
                urlType = GMAIL
                startGmail()
            }
            includeGit.root.setOnClickListener {
                urlType = GIT
                startIntentWithData<WebViewActivity> {
                    putExtra(WebViewIntentKey.URL_TYPE.key, urlType)
                    putExtra(WebViewIntentKey.URL_KEY.key, githubUrl)
                }
                overridePendingTransition(
                    UtilityR.anim.item_animation_slide_from_bottom,
                    UtilityR.anim.anim_stay_still
                )
            }

            includeLinkedIn.root.setOnClickListener {
                urlType = LINKEDIN
                startLinkedIn {
                    //fallback
                    startIntentWithData<WebViewActivity> {
                        putExtra(WebViewIntentKey.URL_TYPE.key, urlType)
                        putExtra(WebViewIntentKey.URL_KEY.key, linkedinUrl)
                    }
                    overridePendingTransition(
                        UtilityR.anim.item_animation_slide_from_bottom,
                        UtilityR.anim.anim_stay_still
                    )
                }
            }
        }
    }

    private fun onItemClick(documentId: String, adapterPosition: Int, sharedView: View) {
        if (documentId.isEmpty()) {
            showFancyToast(getString(UtilityR.string.document_id_empty))
            return
        }
        binding.includeRcExp.rcExperience.let {
            makeSharedSceneTransitionWithDataResult<ExperienceDetailActivity>(
                REQUEST_CODE,
                Pair.create(sharedView, sharedView.transitionName)
            ) {
                putExtra(ExpIntentKeys.DOCUMENT_ID.key, documentId)
                putExtra(ExpIntentKeys.ADAPTER_POS.key, adapterPosition)
            }
        }
    }

    private fun setRc() {
        expAdapter = ExperienceListAdapter(::onItemClick)
        binding.includeRcExp.rcExperience.apply {
            layoutManager = LinearLayoutManager(this@MyResumeActivity)
            adapter = expAdapter
            addItemDecoration(ItemOffsetDecoration(resources.getInteger(UtilityR.integer.item_off_set)))
        }
    }

    private fun hideShimmerAndBindList(data: Experiences?) {
        binding.includeShimmer.root.let {
            it.createAnim(ALPHA, 1f, 0f, shimmerAnimDuration).apply {
                it.stopShimmer()
                it.visibility = View.GONE
                if (data != null) {
                    val expList = data.data
                    binding.includeRcExp.rcExperience.layoutAnimation = recyclerViewAnimation
                    if (!expList.isNullOrEmpty()) {
                        val sortedList = expList.sortedBy {
                            it.priority
                        }
                        expAdapter?.submitList(sortedList)
                    } else {
                        showFancyToast(
                            "Empty Views Will be implemented Later",
                            type = FancyToastTypes.ERROR.value
                        )
                    }
                }
            }
        }
    }

    /**
     * when we exit activity we show associated Views for better animation along with shared element transition
     * @see [showViews]
     * **/
    private fun View.startAnimationSet(isShown: Boolean, onEndAnim: () -> Unit = {}) {
        val startValue = if (isShown) 0f else 1f
        val endValue = if (isShown) 1f else 0f
        val scaleStartValue = if (isShown) 0.5f else 1f
        val fadeIn = createAnim(ALPHA, startValue, endValue)
        val scaleX = createAnim(SCALE_X, scaleStartValue, endValue, duration = HALF_SECOND)
        val scaleY = createAnim(SCALE_Y, scaleStartValue, endValue, duration = HALF_SECOND)
        aniSet = getAnimSet(
            fadeIn,
            scaleX,
            scaleY
        )
        aniSet?.addListener(AnimatorListenerAdapter(
            onEnd = {
                onEndAnim.invoke()
            }
        ))
        aniSet?.start()
    }


    private fun showViews() {
        lifecycleScope.launch {
            delay(300)
            binding.tvExpTitle.startAnimationSet(isShown = true)
            binding.dividerLine.startAnimationSet(isShown = true)
            binding.includeRcExp.root.startAnimationSet(isShown = true)
            binding.includeShimmer.root.startAnimationSet(isShown = true)
            binding.includeResumeContent.root.startAnimationSet(isShown = true)
            binding.includeResumeHeader.apply {
                btnBack.startAnimationSet(isShown = true) {
                    //onEnd->
                    isBackVisible = true
                }
                llAddressContainer.startAnimationSet(isShown = true)
                tvMyName.startAnimationSet(isShown = true)
            }
        }
    }

    private fun hideViews() {
        binding.includeResumeHeader.apply {
            llAddressContainer.hideExpAnimationSet()
            tvMyName.hideExpAnimationSet()
            btnBack.hideExpAnimationSet()
        }
        binding.tvExpTitle.hideExpAnimationSet()
        binding.dividerLine.hideExpAnimationSet()
        binding.includeRcExp.root.hideExpAnimationSet()
        binding.includeShimmer.root.hideExpAnimationSet()
        binding.includeResumeContent.root.hideExpAnimationSet()
    }

    /**
     * when we exit activity we hide associated Views for better animation
     * @see [hideViews]
     * **/
    private fun View.hideExpAnimationSet() {
        val startValue = 0.5f
        val endValue = 0f
        val fadeIn = createAnim(ALPHA, startValue, endValue, duration = 200L)
        startAnimSet(
            fadeIn
        )
    }


    private fun setSharedTransitions() {
        window.sharedElementEnterTransition =
            TransitionUtils.createResumeSharedEnterTransition()
    }

    private fun setJobDescription(data: Profile?) {
        if (data != null) {
            binding.includeResumeHeader.tvMyName.text = data.name
            val string = data.description_two ?: getString(UtilityR.string.resume_about_me)
            setMarkdown(
                string,
                binding.includeResumeContent.tvDescription
            )
        } else {
            binding.includeResumeHeader.tvMyName.text = getString(UtilityR.string.my_default_name)
            setMarkdown(
                getString(UtilityR.string.resume_about_me),
                binding.includeResumeContent.tvDescription
            )
        }

    }

    private fun awaitViewDraw() {
        awaitViewDraw {
            startPostponedEnterTransition()
            binding.includeResumeHeader.llAddressContainer.visibility = View.VISIBLE
        }
    }

    private fun setWindowTransitions() {
        window.enterTransition = TransitionUtils.createResumeEnterTransition(
            this,
            targetViews = arrayOf<View>(
                binding.includeResumeContent.tvDescription,
                binding.includeShimmer.root,
                binding.includeRcExp.rcExperience
            ),
            binding.includeResumeHeader.llAddressContainer,
            binding.includeResumeHeader.ivProfile,
            binding.includeResumeHeader.tvMyName
        )
    }

    private fun setSharedElementNames() {
        binding.includeResumeHeader.ivProfile.transitionName =
            SharedElementsNames.IV_PROFILE_SHARED.name
    }

    override fun setBinding(inflater: LayoutInflater) = ActivityResumeBinding.inflate(inflater)

    override fun handleBackPress() {
        if (isBackVisible) {
            finishActivity()
        }
    }

    private fun finishActivity() {
        lifecycleScope.launch {
            hideViews()
            //to prevent awkward  status color change,
            //used anim update progress   instead of directly change then revert circular reveal animation
            changeStatusColorFromDefaultToSecondary(600L)
            delay(200)
            setResult(RESULT_OK)
            finishAfterTransition()
        }
    }

}