package com.kyrie.myportfolio.setting.faq.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kyrie.myportfolio.databinding.ItemChildFaqBinding
import com.kyrie.utility.utility.setMarkdownWithBullet

class FaqChildAdapter : ListAdapter<String, FaqChildAdapter.FaqChildViewHolder>(FaqChildDiffUtil) {
    inner class FaqChildViewHolder(private val binding: ItemChildFaqBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            val context = binding.root.context
            context.setMarkdownWithBullet(item, binding.tvFaqContent)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FaqChildViewHolder {
        return FaqChildViewHolder(
            ItemChildFaqBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )
    }

    override fun onBindViewHolder(
        holder: FaqChildViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}

object FaqChildDiffUtil : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(
        oldItem: String,
        newItem: String,
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: String,
        newItem: String,
    ): Boolean {
        return oldItem == newItem
    }
}
