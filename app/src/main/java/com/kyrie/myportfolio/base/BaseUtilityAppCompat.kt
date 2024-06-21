package com.kyrie.myportfolio.base

import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.color.MaterialColors
import com.kyrie.utility.animation.createValueAnimatorAnim
import com.kyrie.utility.utility.hideShimmer
import com.kyrie.utility.R as UtilityR

abstract class BaseUtilityAppCompat : AppCompatActivity() {

    protected val shimmerAnimDuration by lazy {
        resources.getInteger(UtilityR.integer.shimmer_duration).toLong()
    }

    protected val secondaryStatusColor by lazy {
        val defaultColor = ContextCompat.getColor(this, UtilityR.color.colorSurfaceSecondary)
        MaterialColors.getColor(
            this,
            com.google.android.material.R.attr.colorSecondary,
            defaultColor
        )
    }

    protected val greyStatusColor by lazy {
        ContextCompat.getColor(this, android.R.color.darker_gray)
    }

    private val resId by lazy {
        UtilityR.anim.layout_animation_slide_from_bottom
    }
    private val resFallDownId by lazy {
        UtilityR.anim.layout_animation_slide_from_top
    }
    private val resFadeScaleOut by lazy {
        com.kyrie.utility.R.anim.layout_animation_fade_scale_out
    }
    private val resFadeScaleIn by lazy {
        com.kyrie.utility.R.anim.layout_animation_fade_scale_in
    }
    protected val recyclerViewAnimation: LayoutAnimationController by lazy {
        AnimationUtils.loadLayoutAnimation(this, resId)
    }
    protected val recyclerFadeScaleOut: LayoutAnimationController by lazy {
        AnimationUtils.loadLayoutAnimation(this, resFadeScaleOut)
    }

    protected val recyclerFadeScaleIn: LayoutAnimationController by lazy {
        AnimationUtils.loadLayoutAnimation(this, resFadeScaleIn)
    }

    protected val recyclerViewExitAnimation: LayoutAnimationController by lazy {
        AnimationUtils.loadLayoutAnimation(this, resFallDownId)
    }

    protected val whiteColor by lazy {
        ContextCompat.getColor(this, UtilityR.color.white)
    }

    open fun changeStatusColorFromWhiteToSecondary(duration: Long) {
        createValueAnimatorAnim(whiteColor, secondaryStatusColor, duration) {
            //changing previous activity's status bar color to look more elegant
            window.statusBarColor = animatedValue as Int
        }
    }

    open fun changeStatusColorFromGreyToSecondary(duration: Long) {
        createValueAnimatorAnim(greyStatusColor, secondaryStatusColor, duration) {
            //changing previous activity's status bar color to look more elegant
            window.statusBarColor = animatedValue as Int
        }
    }

    open fun changeStatusColorFromSecondaryToWhite(duration: Long) {
        createValueAnimatorAnim(secondaryStatusColor, whiteColor, duration) {
            //changing previous activity's status bar color to look more elegant
            window.statusBarColor = animatedValue as Int
        }
    }

    open fun changeStatusColorFromSecondaryToGrey(duration: Long) {
        createValueAnimatorAnim(secondaryStatusColor, greyStatusColor, duration) {
            //changing previous activity's status bar color to look more elegant
            window.statusBarColor = animatedValue as Int
        }
    }

    open fun  hideShimmerAndBindList(
        shimmerLayout: ShimmerFrameLayout,
        bindList: () -> Unit
    ) {
        shimmerLayout.hideShimmer(shimmerAnimDuration) {
            bindList.invoke()
        }
    }

}