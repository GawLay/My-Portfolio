package com.kyrie.myportfolio.setting.attribute

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kyrie.data.models.Attributes
import com.kyrie.myportfolio.databinding.ItemAttributeBinding
import com.kyrie.utility.utility.setHtmlText

class AttributeAdapter : ListAdapter<Attributes, AttributeAdapter.AttributeVH>(AttributeDiffUtil) {
    inner class AttributeVH(private val binding: ItemAttributeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Attributes) {
            binding.tvIconName.text = item.name
            binding.tvAttribute.apply {
                setHtmlText(item.attributeLine)
                movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AttributeVH {
        val binding =
            ItemAttributeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttributeVH(binding)
    }

    override fun onBindViewHolder(
        holder: AttributeVH,
        position: Int,
    ) {
        val item = getItem(position)
        holder.bind(item)
    }
}

object AttributeDiffUtil : DiffUtil.ItemCallback<Attributes>() {
    override fun areItemsTheSame(
        oldItem: Attributes,
        newItem: Attributes,
    ): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(
        oldItem: Attributes,
        newItem: Attributes,
    ): Boolean {
        return oldItem.name == newItem.name && oldItem.attributeLine == newItem.attributeLine
    }
}
