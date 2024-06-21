package com.kyrie.myportfolio.skillChipAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kyrie.myportfolio.databinding.ItemSkillRowBinding
import com.kyrie.utility.utility.showLog


class SkillChipAdapter : ListAdapter<String, SkillChipAdapter.SkillChipVH>(SkillChipDiffUtil) {
    inner class SkillChipVH(private val binding: ItemSkillRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.tvSkillName.text =item
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillChipVH {
        val binding =
            ItemSkillRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SkillChipVH(binding)
    }

    override fun onBindViewHolder(holder: SkillChipVH, position: Int) {
        val item = getItem(position)
        if (item != null)
            holder.bind(item)
    }
}

private object SkillChipDiffUtil : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

}