package com.kyrie.myportfolio.setting.attribute

import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.kyrie.myportfolio.base.BaseActivity
import com.kyrie.myportfolio.databinding.ActivityAttributesBinding
import com.kyrie.utility.utility.ItemOffsetDecoration
import com.kyrie.utility.utility.changeStatusBarColor
import com.kyrie.utility.R as UtilityR

class AttributeActivity : BaseActivity<ActivityAttributesBinding>() {
    private var attributeAdapter: AttributeAdapter? = null
    override fun onCreated(savedInstanceState: Bundle?) {
        setToolbar()
        setRc()
    }

    private fun setToolbar() {
        binding.include.toolbar.apply {

            title = getString(UtilityR.string.title_attribution)
            setNavigationOnClickListener {
                handleBackPress()
            }
        }
    }

    private fun setRc() {
        attributeAdapter = AttributeAdapter()
        binding.rcAttributes.apply {
            layoutManager = LinearLayoutManager(this@AttributeActivity)
            layoutAnimation = recyclerViewAnimation
            adapter = attributeAdapter
            addItemDecoration(ItemOffsetDecoration(12))
        }
        val list = AttributeList.attributesList
        attributeAdapter?.submitList(list)
    }

    override fun setBinding(inflater: LayoutInflater) = ActivityAttributesBinding.inflate(inflater)

    override fun handleBackPress() {
        finish()
        overridePendingTransition(UtilityR.anim.anim_stay_still,UtilityR.anim.item_animation_slide_from_top)
    }
}

