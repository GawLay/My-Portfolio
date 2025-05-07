package com.kyrie.myportfolio.skill

import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.kyrie.data.models.SkillsData
import com.kyrie.data.remote.State
import com.kyrie.myportfolio.base.BaseRevealActivity
import com.kyrie.myportfolio.databinding.ActivitySkillBinding
import com.kyrie.utility.constants.FancyToastTypes
import com.kyrie.utility.utility.ItemOffsetDecoration
import com.kyrie.utility.utility.showFancyToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.kyrie.utility.R as UtilityR

class SkillActivity : BaseRevealActivity<ActivitySkillBinding>() {
    private val spanCount = 3
    private var skillItemAdapter: SkillItemAdapter? = null
    private val viewModel: SkillViewModel by viewModel()

    override fun onCreated(savedInstanceState: Bundle?) {
        changeStatusColorFromSecondaryToDefault(750L)
        setupRc()
        getSkills()
        binding.includeToolbar.toolbarGeneric.title = getString(UtilityR.string.title_skill)
    }

    override fun onClickEvents() {
        binding.includeToolbar.toolbarGeneric.setNavigationOnClickListener {
            handleBackPress()
        }
    }

    private fun getSkills() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getSkills().collectLatest { state ->
                    when (state) {
                        is State.Loading -> {
//                            showFancyToast(
//                                "Loading Views Will be implemented Later",
//                                type = FancyToastTypes.CONFUSING.value
//                            )
                        }

                        is State.Failed -> {
                            showFancyToast(
                                "Failed Views Will be implemented Later",
                                type = FancyToastTypes.ERROR.value,
                            )
                        }

                        is State.Success -> {
                            val data = state.data
                            val skillList = data?.data
                            if (!skillList.isNullOrEmpty()) {
                                val sortedList =
                                    skillList.sortedBy {
                                        it.priority
                                    }
                                setSkillList(sortedList)
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

    private fun setSkillList(skillList: List<SkillsData>?) {
        binding.rcSkills.layoutAnimation = recyclerViewAnimation
        skillItemAdapter?.submitList(skillList)
    }

    private fun setupRc() {
        skillItemAdapter = SkillItemAdapter()

        binding.rcSkills.apply {
            layoutManager = GridLayoutManager(this@SkillActivity, spanCount)
            adapter = skillItemAdapter
            addItemDecoration(ItemOffsetDecoration(resources.getInteger(UtilityR.integer.item_off_set)))
        }
    }

    override fun setBinding(inflater: LayoutInflater) = ActivitySkillBinding.inflate(inflater)

    override fun handleBackPress() {
        lifecycleScope.launch {
            delay(200)
            changeStatusColorFromDefaultToSecondary(200L)
            revealAnimation?.unRevealActivity()
        }
    }
}
