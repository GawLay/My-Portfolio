package com.kyrie.myportfolio.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isNotEmpty
import androidx.core.view.updateLayoutParams
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.circularreveal.CircularRevealFrameLayout
import com.google.android.material.color.MaterialColors
import com.kyrie.utility.animation.createValueAnimatorAnim
import com.kyrie.utility.annotation.DoNotImplementDirectly
import com.kyrie.utility.utility.ThemeUtil
import com.kyrie.utility.utility.applyNavigationBarInsets
import com.kyrie.utility.utility.applyStatusBarInsets
import com.kyrie.utility.utility.hideShimmer
import com.kyrie.utilit as UtilityR

@DoNotImplementDirectly
abstract class BaseUtilityAppCompat : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        super.onCreate(savedInstanceState)
    }

    protected fun applyEdgeToEdgeInsets(root: View) {
        when (root) {
            is MaterialToolbar -> root.applyStatusBarInsets()
            is BottomNavigationView -> root.applyNavigationBarInsets()
            is CircularRevealFrameLayout -> root.applyNavigationBarInsets()
            is RecyclerView -> root.applyNavigationBarInsets()
            is ViewPager2 -> root.applyNavigationBarInsets()
            is NestedScrollView -> {
                root.applyNavigationBarInsets()
                val firstChild = if (root.isNotEmpty()) root.getChildAt(0) else null
                firstChild?.applyStatusBarInsets()
            }
            is ScrollView -> root.applyNavigationBarInsets()
        }
        if (root is ViewGroup) {
            for (i in 0 until root.childCount) {
                applyEdgeToEdgeInsets(root.getChildAt(i))
            }
        }
    }

    protected val shimmerAnimDuration by lazy {
        resources.getInteger(UtilityR.integer.shimmer_duration).toLong()
    }

    protected val secondaryStatusColor by lazy {
        val defaultColor = ContextCompat.getColor(this, UtilityR.color.colorSurfaceSecondary)
        MaterialColors.getColor(
            this,
            com.google.android.material.R.attr.colorSecondary,
            defaultColor,
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
    protected val blackColor by lazy {
        ContextCompat.getColor(this, UtilityR.color.black)
    }

    open fun changeStatusColorFromDefaultToSecondary(duration: Long) {
        val fromColor =
            if (ThemeUtil.isNightMode(this)) {
                blackColor
            } else {
                whiteColor
            }
        createValueAnimatorAnim(fromColor, secondaryStatusColor, duration) {
            // changing previous activity's status bar color to look more elegant
            window.statusBarColor = animatedValue as Int
        }
    }

    open fun changeStatusColorFromGreyToSecondary(duration: Long) {
        createValueAnimatorAnim(greyStatusColor, secondaryStatusColor, duration) {
            // changing previous activity's status bar color to look more elegant
            window.statusBarColor = animatedValue as Int
        }
    }

    open fun changeStatusColorFromSecondaryToDefault(duration: Long) {
        val toColor =
            if (ThemeUtil.isNightMode(this)) {
                blackColor
            } else {
                whiteColor
            }
        createValueAnimatorAnim(secondaryStatusColor, toColor, duration) {
            // changing previous activity's status bar color to look more elegant
            window.statusBarColor = animatedValue as Int
        }
    }

    open fun changeStatusColorFromSecondaryToGrey(duration: Long) {
        createValueAnimatorAnim(secondaryStatusColor, greyStatusColor, duration) {
            // changing previous activity's status bar color to look more elegant
            window.statusBarColor = animatedValue as Int
        }
    }

    protected fun View.applyTopInsetAsHeight(additionalHeight: Int) {
        ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams {
                height = additionalHeight + systemBars.top
            }
            insets
        }
    }

    protected fun View.applyTopInsetAsMargin(additionalMargin: Int) {
        ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = additionalMargin + systemBars.top
            }
            insets
        }
    }

    open fun hideShimmerAndBindList(
        shimmerLayout: ShimmerFrameLayout,
        bindList: () -> Unit,
    ) {
        shimmerLayout.hideShimmer(shimmerAnimDuration) {
            bindList.invoke()
        }
    }
}
