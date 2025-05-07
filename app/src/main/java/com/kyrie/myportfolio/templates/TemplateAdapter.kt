package com.kyrie.myportfolio.templates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kyrie.data.models.TemplateData
import com.kyrie.myportfolio.databinding.ItemTemplateBinding
import com.kyrie.utility.utility.loadUrl
import com.kyrie.utility.utility.setSafeOnClickListener

class TemplateAdapter(
    private val onClick: (String) -> Unit,
    private val onDownloadClick: (String) -> Unit,
) :
    ListAdapter<TemplateData, TemplateAdapter.TemplateHolder>(TemplateDiffUtil) {
    private lateinit var holder: TemplateHolder

    override fun onCreateViewHolder(
        parent: ViewGroup,
        p1: Int,
    ): TemplateHolder {
        val binding =
            ItemTemplateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TemplateHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TemplateHolder,
        position: Int,
    ) {
        this.holder = holder
        holder.bind(getItem(position), onClick, onDownloadClick)
    }

    class TemplateHolder(private val binding: ItemTemplateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: TemplateData,
            onClick: (String) -> Unit,
            onDownloadClick: (String) -> Unit,
        ) {
            binding.tvTemplateName.text = item.name
            binding.tvTemplateInfo.text = item.description
            binding.ivTemplatePreview.loadUrl(item.image_url ?: "")
            binding.root.setSafeOnClickListener {
                onClick.invoke(item.file_name ?: "")
            }
            binding.includeDownloadGroup.imgBtnDownload.setSafeOnClickListener {
                onDownloadClick.invoke(item.file_name ?: "")
            }
        }
    }

    object TemplateDiffUtil : DiffUtil.ItemCallback<TemplateData>() {
        override fun areItemsTheSame(
            oldItem: TemplateData,
            newItem: TemplateData,
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: TemplateData,
            newItem: TemplateData,
        ): Boolean {
            return oldItem.name == newItem.name &&
                oldItem.file_name == newItem.file_name && oldItem.image_url == newItem.image_url
        }
    }
}
