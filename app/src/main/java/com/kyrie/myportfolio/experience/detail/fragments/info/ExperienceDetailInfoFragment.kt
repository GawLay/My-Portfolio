package com.kyrie.myportfolio.experience.detail.fragments.info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.kyrie.data.models.ExperienceDetail
import com.kyrie.myportfolio.base.BaseFragment
import com.kyrie.myportfolio.databinding.FragmentExperienceDetailInfoBinding
import com.kyrie.myportfolio.experience.detail.ExperienceDetailAdapter
import com.kyrie.myportfolio.experience.detail.ExperienceDetailViewModel
import com.kyrie.myportfolio.experience.detail.SectionHeaderAdapter
import com.kyrie.myportfolio.skillChipAdapter.SkillChipAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import com.kyrie.utility.R as UtilityR

class ExperienceDetailInfoFragment : BaseFragment<FragmentExperienceDetailInfoBinding>() {
    private val viewModel: ExperienceDetailViewModel by activityViewModel()
    private var expDetailAdapter: ExperienceDetailAdapter? = null
    private var skillChipAdapter: SkillChipAdapter? = null
    private var skillHeaderAdapter: SectionHeaderAdapter? = null

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentExperienceDetailInfoBinding {
        return FragmentExperienceDetailInfoBinding.inflate(inflater, container, false)
    }

    override fun initViews() {
        super.initViews()
        setRc()
    }

    override fun observe() {
        super.observe()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.expDetail.collectLatest {
                bindData(it)
            }
        }
    }

    private fun setRc() {
        val expAdapter = ExperienceDetailAdapter().also { expDetailAdapter = it }
        val headerAdapter = SectionHeaderAdapter(getString(UtilityR.string.skill)).also { skillHeaderAdapter = it }
        val chipAdapter = SkillChipAdapter().also { skillChipAdapter = it }

        // A single scrolling list: full-width project rows, a "Skills" header, then
        // wrapping skill chips. FlexboxLayoutManager lets both layouts share one RecyclerView.
        val concatAdapter = ConcatAdapter(expAdapter, headerAdapter, chipAdapter)
        binding.rcContent.apply {
            layoutManager =
                FlexboxLayoutManager(requireContext()).apply { flexDirection = FlexDirection.ROW }
            adapter = concatAdapter
        }
    }

    private fun bindData(data: ExperienceDetail?) {
        val skills = data?.skills
        skillHeaderAdapter?.setVisible(!skills.isNullOrEmpty())
        skillChipAdapter?.submitList(skills)
        expDetailAdapter?.submitList(data?.projects)
        binding.rcContent.layoutAnimation = recyclerViewAnimation
        binding.rcContent.scheduleLayoutAnimation()
    }
}
