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

/**
 * Reads [url] straight from Glide's cache and reports back as soon as it is on screen, so a
 * caller can hold a postponed shared element transition until the image is in place.
 * On a cache miss it falls back to a normal load rather than leaving the view empty.
 */
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
                // Never start a second request from inside Glide's own callback.
                post { loadUrl(url) }
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
