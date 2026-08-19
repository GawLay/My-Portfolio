package com.kyrie.myportfolio.experience.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.kyrie.myportfolio.databinding.ItemExpSectionHeaderBinding

/**
 * A single-item adapter that renders a full-width section title inside the shared
 * [ConcatAdapter]. It reports zero items until [setVisible] turns it on, so the header
 * only appears once its section actually has content.
 */
class SectionHeaderAdapter(private val title: String) :
    RecyclerView.Adapter<SectionHeaderAdapter.HeaderVH>() {
    private var visible = false

    fun setVisible(value: Boolean) {
        if (visible == value) return
        visible = value
        if (value) notifyItemInserted(0) else notifyItemRemoved(0)
    }

    inner class HeaderVH(private val binding: ItemExpSectionHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.tvSectionTitle.text = title
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): HeaderVH {
        val binding =
            ItemExpSectionHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeaderVH(binding)
    }

    override fun onBindViewHolder(
        holder: HeaderVH,
        position: Int,
    ) = holder.bind()

    override fun getItemCount(): Int = if (visible) 1 else 0

    override fun onViewAttachedToWindow(holder: HeaderVH) {
        super.onViewAttachedToWindow(holder)
        // Force the header to occupy a full flex row above the wrapping skill chips.
        (holder.itemView.layoutParams as? FlexboxLayoutManager.LayoutParams)?.flexBasisPercent = 1f
    }
}
