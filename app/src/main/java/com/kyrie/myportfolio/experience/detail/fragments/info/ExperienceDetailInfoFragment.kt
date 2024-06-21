package com.kyrie.myportfolio.experience.detail.fragments.info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.kyrie.data.models.ExperienceDetail
import com.kyrie.myportfolio.base.BaseFragment
import com.kyrie.myportfolio.databinding.FragmentExperienceDetailInfoBinding
import com.kyrie.myportfolio.experience.detail.ExperienceDetailAdapter
import com.kyrie.myportfolio.experience.detail.ExperienceDetailViewModel
import com.kyrie.myportfolio.skillChipAdapter.SkillChipAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class ExperienceDetailInfoFragment : BaseFragment<FragmentExperienceDetailInfoBinding>() {

    private val viewModel: ExperienceDetailViewModel by activityViewModel()
    private var expDetailAdapter: ExperienceDetailAdapter? = null
    private var skillChipAdapter: SkillChipAdapter? = null


    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
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
        expDetailAdapter = ExperienceDetailAdapter()
        skillChipAdapter = SkillChipAdapter()
        binding.rcExperience.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = expDetailAdapter
        }
        FlexboxLayoutManager(requireContext()).let {
            it.flexDirection = FlexDirection.ROW
            binding.rcSkills.layoutManager = it
            binding.rcSkills.adapter = skillChipAdapter
        }
    }

    private fun bindData(data: ExperienceDetail?) {
        binding.rcExperience.layoutAnimation = recyclerViewAnimation
        binding.rcSkills.layoutAnimation = recyclerViewAnimation
        skillChipAdapter?.submitList(data?.skills)
        expDetailAdapter?.submitList(data?.projects)
    }
}