package com.kyrie.myportfolio.experience.detail.vpAdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kyrie.myportfolio.experience.detail.fragments.info.ExperienceDetailInfoFragment

class ExpDetailVPAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 1

    override fun createFragment(position: Int): Fragment {
       return ExperienceDetailInfoFragment()
    }
}