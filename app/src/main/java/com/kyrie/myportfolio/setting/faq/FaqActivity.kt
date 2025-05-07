package com.kyrie.myportfolio.setting.faq

import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kyrie.data.remote.State
import com.kyrie.myportfolio.base.BaseActivity
import com.kyrie.myportfolio.databinding.ActivityFaqBinding
import com.kyrie.myportfolio.setting.faq.adapter.FaqAdapter
import com.kyrie.utility.R
import com.kyrie.utility.constants.FancyToastTypes
import com.kyrie.utility.utility.ItemOffsetDecoration
import com.kyrie.utility.utility.overridePendingTransitionExt
import com.kyrie.utility.utility.showFancyToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FaqActivity : BaseActivity<ActivityFaqBinding>() {
    private val viewModel: FaqViewModel by viewModel()
    private lateinit var faqAdapter: FaqAdapter

    override fun onCreated(savedInstanceState: Bundle?) {
        setupToolbar()
        setupRc()
        getFaqList()
    }

    private fun setupRc() {
        faqAdapter = FaqAdapter()
        binding.rcFaq.apply {
            layoutManager = LinearLayoutManager(this@FaqActivity)
            layoutAnimation = recyclerViewAnimation
            adapter = faqAdapter
            addItemDecoration(ItemOffsetDecoration(resources.getInteger(R.integer.item_off_set)))
        }
    }

    private fun getFaqList() {
        lifecycleScope.launch {
            viewModel.getFaqList().collectLatest { state ->
                when (state) {
                    is State.Loading -> {
                        binding.includeShimmer.root.startShimmer()
                    }

                    is State.Failed -> {
                        showFancyToast(state.message, FancyToastTypes.ERROR.value)
                    }

                    is State.Success -> {
                        hideShimmerAndBindList(binding.includeShimmer.root) {
                            val faqInfo = state.data
                            val faqList = faqInfo?.data
                            if (!faqList.isNullOrEmpty()) {
                                faqAdapter.submitList(faqList)
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

    private fun setupToolbar() {
        binding.includeToolbar.toolbarGeneric.apply {
            title = getString(R.string.title_faq)
            setNavigationOnClickListener {
                handleBackPress()
            }
        }
    }

    override fun setBinding(inflater: LayoutInflater) = ActivityFaqBinding.inflate(inflater)

    override fun handleBackPress() {
        finish()
        overridePendingTransitionExt(
            true,
            R.anim.anim_stay_still,
            R.anim.item_animation_slide_from_top,
        )
    }
}
