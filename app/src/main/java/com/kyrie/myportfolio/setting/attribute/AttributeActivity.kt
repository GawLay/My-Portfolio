package com.kyrie.myportfolio.setting.attribute

import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kyrie.data.remote.State
import com.kyrie.myportfolio.base.BaseActivity
import com.kyrie.myportfolio.databinding.ActivityAttributesBinding
import com.kyrie.utility.constants.FancyToastTypes
import com.kyrie.utility.utility.ItemOffsetDecoration
import com.kyrie.utility.utility.overridePendingTransitionExt
import com.kyrie.utility.utility.showFancyToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.kyrie.utility.R as UtilityR

class AttributeActivity : BaseActivity<ActivityAttributesBinding>() {
    private val viewModel: AttributeViewModel by viewModel()
    private var attributeAdapter: AttributeAdapter? = null

    override fun onCreated(savedInstanceState: Bundle?) {
        setToolbar()
        setRc()
        getAttributeList()
    }

    private fun setToolbar() {
        binding.includeToolbar.toolbarGeneric.apply {
            title = getString(UtilityR.string.title_attribution)
            setNavigationOnClickListener {
                handleBackPress()
            }
        }
    }

    private fun setRc() {
        // The shimmer layout ships at alpha 0 (see shimmer_attribute_list.xml); make it visible so
        // the loading placeholder actually shows while the attribute list is being fetched.
        binding.includeShimmer.root.alpha = 1f
        attributeAdapter = AttributeAdapter()
        binding.rcAttributes.apply {
            layoutManager = LinearLayoutManager(this@AttributeActivity)
            layoutAnimation = recyclerViewAnimation
            adapter = attributeAdapter
            addItemDecoration(ItemOffsetDecoration(12))
        }
    }

    private fun getAttributeList() {
        lifecycleScope.launch {
            viewModel.getAttributeList().collectLatest { state ->
                when (state) {
                    is State.Loading -> {
                        binding.includeShimmer.root.startShimmer()
                    }

                    is State.Failed -> {
                        showFancyToast(state.message, FancyToastTypes.ERROR.value)
                    }

                    is State.Success -> {
                        hideShimmerAndBindList(binding.includeShimmer.root) {
                            val attributeList = state.data?.data
                            if (!attributeList.isNullOrEmpty()) {
                                attributeAdapter?.submitList(attributeList)
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

    override fun setBinding(inflater: LayoutInflater) = ActivityAttributesBinding.inflate(inflater)

    override fun handleBackPress() {
        finish()
        overridePendingTransitionExt(
            true,
            UtilityR.anim.anim_stay_still,
            UtilityR.anim.item_animation_slide_from_top,
        )
    }
}
