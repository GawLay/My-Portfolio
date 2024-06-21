package com.kyrie.myportfolio.experience.detail

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.kyrie.data.models.ExpDetailsData
import com.kyrie.myportfolio.databinding.ItemExperienceDetailBinding
import com.kyrie.myportfolio.databinding.ItemRcExperienceBinding
import com.kyrie.myportfolio.skillChipAdapter.SkillChipAdapter

class ExperienceDetailAdapter() :
    ListAdapter<ExpDetailsData, ExperienceDetailAdapter.ExpDetailVH>(ExpDetailDiffUtil) {
    inner class ExpDetailVH(private val binding: ItemExperienceDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(items: ExpDetailsData) {
            val context = binding.root.context
            binding.tvItemPName.text = items.name
            val descAdapter = ExperienceDescriptionAdapter()
            binding.rcItemExp.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = descAdapter
            }
            descAdapter.submitList(items.data)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpDetailVH {
        val binding =
            ItemExperienceDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpDetailVH(binding)
    }

    override fun onBindViewHolder(holder: ExpDetailVH, position: Int) {
        val items = getItem(position)
        if (items != null)
            holder.bind(items)
    }
}

private object ExpDetailDiffUtil : DiffUtil.ItemCallback<ExpDetailsData>() {
    override fun areItemsTheSame(oldItem: ExpDetailsData, newItem: ExpDetailsData): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: ExpDetailsData, newItem: ExpDetailsData): Boolean {
        return oldItem.name == newItem.name && oldItem.data == newItem.data
    }

}