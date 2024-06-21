package com.kyrie.myportfolio.experience.detail

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kyrie.myportfolio.databinding.ItemChildExperienceBinding
import com.kyrie.myportfolio.webView.WebViewActivity
import com.kyrie.utility.constants.WebViewIntentKey
import com.kyrie.utility.utility.isHtmlText
import com.kyrie.utility.utility.isHttpLink
import com.kyrie.utility.utility.isPlayStoreLink
import com.kyrie.utility.utility.startIntentWithData
import com.kyrie.utility.utility.startPlayStore
import com.kyrie.utility.R as UtilityR

class ExperienceDescriptionAdapter :
    ListAdapter<String, ExperienceDescriptionAdapter.ExpDescriptionVH>(ExpDescDiffUtil) {
    inner class ExpDescriptionVH(private val binding: ItemChildExperienceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            val context = binding.root.context
            if (item.isHtmlText() || item.isHttpLink()) {
                handleClickUrl(item, context)
            } else {
                binding.tvDescription.text = item
            }
        }

        private fun handleClickUrl(url: String, context: Context) {
            binding.tvDescription.movementMethod = LinkMovementMethod.getInstance()
            val spannableString = SpannableString(url)
            val secondaryColor = ContextCompat.getColor(context, UtilityR.color.md_theme_secondary);

            // Create a ClickableSpan
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    if (url.isPlayStoreLink()) {
                        //redirect to play store
                        context.startPlayStore(url) {
                            //package not found
                            context.startIntentWithData<WebViewActivity> {
                                putExtra(WebViewIntentKey.URL_KEY.key, url)
                            }
                        }
                    } else {
                        //simple web link url
                        context.startIntentWithData<WebViewActivity> {
                            putExtra(WebViewIntentKey.URL_KEY.key, url)
                        }
                    }
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = true
                    ds.color = secondaryColor
                }
            }
            spannableString.setSpan(clickableSpan, 0, url.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.tvDescription.text = spannableString
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpDescriptionVH {
        val binding =
            ItemChildExperienceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpDescriptionVH(binding)
    }

    override fun onBindViewHolder(holder: ExpDescriptionVH, position: Int) {
        val item = getItem(position)
        if (item != null)
            holder.bind(item)
    }
}

private object ExpDescDiffUtil : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

}