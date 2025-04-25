package com.kyrie.myportfolio.setting.faq.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kyrie.data.models.FaqData
import com.kyrie.myportfolio.databinding.ItemFaqBinding
import com.kyrie.utility.R
import com.kyrie.utility.animation.HALF_SECOND
import com.kyrie.utility.animation.ROTATE
import com.kyrie.utility.animation.createAnim
import com.kyrie.utility.utility.ItemOffsetDecoration
import com.kyrie.utility.utility.containsMarkdown
import com.kyrie.utility.utility.setMarkdown
import com.kyrie.utility.utility.setSafeOnClickListener

class FaqAdapter : ListAdapter<FaqData, FaqAdapter.FaqViewHolder>(FaqDiffUtil) {

    inner class FaqViewHolder(private val binding: ItemFaqBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FaqData) {
            val faqChildAdapter = FaqChildAdapter()
            val context = binding.root.context
            val answerList = item.answers
            val question = item.question ?: ""
            binding.imgBtnExpand.setSafeOnClickListener {
                binding.llFaqTitleContainer.performClick()
            }
            binding.llFaqTitleContainer.setSafeOnClickListener {
                item.isExpanded = !item.isExpanded
                notifyItemChanged(bindingAdapterPosition)
            }
            val colorSecondaryTransparent =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorSecondaryTransparent_36) )
            val colorWhite = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white) )
            val colorBlack = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black) )

            val rotationStartValue: Float
            val rotationEndValue: Float
            val backgroundColor:ColorStateList
            if (item.isExpanded) {
                binding.tvFaqTitle.setTextColor(colorWhite)
                rotationStartValue = 90f
                rotationEndValue = 0f
                backgroundColor = colorSecondaryTransparent
            } else {
                binding.tvFaqTitle.setTextColor(colorBlack)
                rotationStartValue = 0f
                rotationEndValue = 90f
                backgroundColor = colorWhite
            }
            binding.imgBtnExpand.createAnim(ROTATE, rotationStartValue, rotationEndValue, HALF_SECOND).apply {
                start()
            }.doOnEnd {
                binding.imgBtnExpand.rotation = rotationEndValue
            }
            binding.root.backgroundTintList = backgroundColor

            binding.rcFaqContent.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
            if (question.containsMarkdown()) {
                context.setMarkdown(question, binding.tvFaqTitle)
            } else {
                binding.tvFaqTitle.text = question
            }
            binding.rcFaqContent.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = faqChildAdapter
                faqChildAdapter.submitList(answerList)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        return FaqViewHolder(
            ItemFaqBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object FaqDiffUtil : DiffUtil.ItemCallback<FaqData>() {
    override fun areItemsTheSame(oldItem: FaqData, newItem: FaqData): Boolean {
        return oldItem.question == newItem.question
    }

    override fun areContentsTheSame(oldItem: FaqData, newItem: FaqData): Boolean {
        return oldItem.question == newItem.question &&
                oldItem.answers.size == newItem.answers.size
    }

}