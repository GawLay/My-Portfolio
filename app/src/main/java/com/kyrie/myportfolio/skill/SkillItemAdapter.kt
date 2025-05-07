package com.kyrie.myportfolio.skill

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kyrie.data.models.SkillsData
import com.kyrie.myportfolio.databinding.ItemSkillCardBinding
import com.kyrie.utility.utility.loadUrl

class SkillItemAdapter :
    ListAdapter<SkillsData, SkillItemAdapter.SkillsViewHolder>(SkillItemDiffUtils) {
    private var urlList = arrayListOf<String>()

    inner class SkillsViewHolder(private val binding: ItemSkillCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SkillsData) {
            binding.ivSkillIcon.loadUrl(data.url ?: "")
            binding.tvSkill.text = data.name
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SkillsViewHolder {
        val binding = ItemSkillCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SkillsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SkillsViewHolder,
        position: Int,
    ) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }
}

private object SkillItemDiffUtils : DiffUtil.ItemCallback<SkillsData>() {
    override fun areItemsTheSame(
        oldItem: SkillsData,
        newItem: SkillsData,
    ): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(
        oldItem: SkillsData,
        newItem: SkillsData,
    ): Boolean {
        return oldItem.url == newItem.url
    }
}
