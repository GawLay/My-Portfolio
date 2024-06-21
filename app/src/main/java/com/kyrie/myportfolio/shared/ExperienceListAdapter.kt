package com.kyrie.myportfolio.shared

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.kyrie.data.models.ExperiencesData
import com.kyrie.myportfolio.databinding.ItemRcExperienceBinding
import com.kyrie.utility.constants.SharedElementsNames

class ExperienceListAdapter(private val onItemClick: (String, Int, View) -> Unit) :
    ListAdapter<ExperiencesData, ExperienceListAdapter.ExperienceListVH>(ExpDiffUtil) {
    inner class ExperienceListVH(private val binding: ItemRcExperienceBinding) :
        ViewHolder(binding.root) {
        fun bind(data: ExperiencesData) {
            val context = binding.root.context
            binding.root.transitionName = SharedElementsNames.JOB_CARD_SHARED.name + adapterPosition
            binding.apply {
                root.setOnClickListener {
                    onItemClick.invoke(data.id ?: "", adapterPosition, binding.root)
                }
                tvCompanyName.text = data.jobTitle
                tvLocation.text = data.location
                tvPosition.text = data.position
                tvTimestamp.text = data.timestamp
                tvJobType.text = data.jobType
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExperienceListVH {
        val context = parent.context
        val binding = ItemRcExperienceBinding.inflate(LayoutInflater.from(context), parent, false)
        return ExperienceListVH(binding)
    }

    override fun onBindViewHolder(holder: ExperienceListVH, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }
}

object ExpDiffUtil : DiffUtil.ItemCallback<ExperiencesData>() {
    override fun areItemsTheSame(oldItem: ExperiencesData, newItem: ExperiencesData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ExperiencesData, newItem: ExperiencesData): Boolean {
        return oldItem.id == newItem.id && oldItem.jobTitle == newItem.jobTitle &&
                oldItem.jobType == newItem.jobType &&
                oldItem.location == newItem.location &&
                oldItem.timestamp == newItem.timestamp &&
                oldItem.position == newItem.position
    }

}