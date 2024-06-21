package com.kyrie.utility.animation

import android.content.Context
import android.transition.*
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout

object TransitionUtils {
    fun createResumeSharedEnterTransition(): TransitionSet {
        val slide = Slide(Gravity.BOTTOM).apply {
            startDelay = 100
        }
        val arcMotion = ArcMotion().apply {
            minimumHorizontalAngle = 20f
            minimumVerticalAngle = 90f
        }
        val changeBound = ChangeBounds().apply {
            startDelay = 200
            pathMotion = arcMotion
        }

        val fade = Fade().apply {
            startDelay = 100
        }
        val imageSet = transitionTogether {
            this += slide
            this += changeBound
            this += fade
        }
        val textSet = transitionTogether {
            this += slide
            this += fade
        }
        return transitionTogether {
            excludeTarget(android.R.id.navigationBarBackground, true)
            excludeTarget(android.R.id.statusBarBackground, true)
            this += imageSet
            this += textSet
        }

    }

    fun getExpDetailWindowTransition(
        context: Context,
        bgHeader: View,
//        tab: TabLayout,
        vp2: ViewPager2
    ): TransitionSet {
        val fade = Fade()
        val clipBounds = ChangeClipBounds()
        val changeBounds = ChangeBounds()
        val slideTop = Slide(Gravity.TOP).apply {
            addTarget(bgHeader)
        }
        val slide = Slide(Gravity.BOTTOM).apply {
            interpolator = AnimationUtils.loadInterpolator(
                context,
                android.R.interpolator.linear_out_slow_in
            )
            startDelay = 300
//            addTarget(tab)
            addTarget(vp2)
        }

        return transitionTogether {
            this += changeBounds
            this += fade
            this += slide
            this += slideTop
            this += clipBounds
            excludeTarget(android.R.id.statusBarBackground, true)
            excludeTarget(android.R.id.navigationBarBackground, true)
        }
    }


    fun createExpDetailSharedEnterTransition(): TransitionSet {
        val slide = Slide(Gravity.BOTTOM).apply {
            startDelay = 100
        }
        val changeBound = ChangeBounds().apply {
            startDelay = 100
        }

//        val recolor = Recolor()
//        recolor.addTarget(root)

        val changeImageTransform = ChangeImageTransform().apply {
            startDelay = 100
        }

        val fade = Fade().apply {
            startDelay = 100
        }
        val imageSet = transitionTogether {
            this += slide
            this += changeBound
            this += fade
        }
        val textSet = transitionTogether {
            this += slide
            this += fade
        }
        return transitionTogether {
            excludeTarget(android.R.id.navigationBarBackground, true)
            excludeTarget(android.R.id.statusBarBackground, true)
//            this += recolor
            this += imageSet
            this += textSet
            this += changeImageTransform
        }

    }

    fun createResumeEnterTransition(
        context: Context,
        targetViews: Array<View>,
        vararg excludeViews: View
    ): TransitionSet {
        val clipBounds = ChangeClipBounds()
        val changeBounds = ChangeBounds()
        val fade = Fade()
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        fade.excludeTarget(android.R.id.navigationBarBackground, true)

        val slide = Slide(Gravity.BOTTOM).apply {
            interpolator = AnimationUtils.loadInterpolator(
                context,
                android.R.interpolator.linear_out_slow_in
            )
            startDelay = 300

        }
        return transitionTogether {
            val iterator = targetViews.iterator()
            while (iterator.hasNext()) {
                val targetView = iterator.next()
                addTarget(targetView)
            }
            this += changeBounds
            this += fade
            this += slide
            this += clipBounds
            for (excludeView in excludeViews) {
                excludeTarget(excludeView, true)
            }
        }
    }

}