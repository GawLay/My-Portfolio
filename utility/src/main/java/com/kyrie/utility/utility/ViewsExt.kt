package com.kyrie.utility.utility

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.view.forEach
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kyrie.utility.animation.ALPHA
import com.kyrie.utility.animation.createAnim


fun ImageView.loadUrl(url: String) {
    Glide.with(this.context).load(url).transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

fun ImageView.loadUrlWithResourceReady(url: String, onResourceReady: () -> Unit) {
    val requestOptions = RequestOptions()
        .dontTransform()
        .onlyRetrieveFromCache(true)
    Glide
        .with(this)
        .load(url)
        .apply(requestOptions)
        .listener(object : RequestListener<Drawable?> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable?>?,
                isFirstResource: Boolean
            ): Boolean {
                onResourceReady.invoke()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable?>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                onResourceReady.invoke()
                return false
            }

        })
        .into(this)
}

fun BottomNavigationView.toggleItemClick(isEnable: Boolean) {
    if (isEnable) {
        menu.forEach { it.isEnabled = true }
    } else {
        menu.forEach { it.isEnabled = false }
    }
}

fun ShimmerFrameLayout.hideShimmer(shimmerAnimDuration: Long, afterShimmerHide: () -> Unit) {

    createAnim(ALPHA, 1f, 0f, shimmerAnimDuration).apply {
        stopShimmer()
        visibility = View.GONE
        afterShimmerHide.invoke()

    }
}
