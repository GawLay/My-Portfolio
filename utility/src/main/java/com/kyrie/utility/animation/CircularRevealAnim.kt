package com.kyrie.utility.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import com.kyrie.utility.utility.overridePendingTransitionExt
import com.kyrie.utility.utility.showLog

class CircularRevealAnim(
    private val mRootView: View,
    intent: Intent,
    private val mActivity: Activity
) {

    private var revealX: Int = 0
    private var revealY: Int = 0

    init {

        if (
            intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) &&
            intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)
        ) {
//            mView.visibility = View.INVISIBLE

            revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0)
            revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0)
            showLog("reveal X value $revealX  Y value $revealY")
            val viewTreeObserver = mRootView.viewTreeObserver
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        revealActivity(revealX, revealY)
                        mRootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }
        }
    }

    //destination radius(rootLayout width height)
    //init circularReveal
    //make *rootLayout visible*
    //then start
    fun revealActivity(x: Int, y: Int) {
        val finalRadius = (mRootView.width.coerceAtLeast(mRootView.height) * 1.1).toFloat()

        // create the animator for this view (the start radius is zero)
        val circularReveal =
            ViewAnimationUtils.createCircularReveal(mRootView, x, y, 0f, finalRadius)
        circularReveal.duration = 600
        circularReveal.interpolator = AccelerateInterpolator()

        // make the view visible and start the animation
        mRootView.visibility = View.VISIBLE
        circularReveal.start()
    }

    //reverse reveal animation
    //for reverse circular
    //reverse start and end(final)value
    fun unRevealActivity(duration: Long = 500) {
        val finalRadius = (mRootView.width.coerceAtLeast(mRootView.height) * 1.1).toFloat()
        val circularReveal = ViewAnimationUtils.createCircularReveal(
            mRootView, revealX, revealY, finalRadius, 0f
        )

        circularReveal.duration = duration
        circularReveal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mRootView.visibility = View.INVISIBLE
                mActivity.finish()
                mActivity.overridePendingTransitionExt(
                    true,
                    0,
                    0
                )
            }
        })
        circularReveal.start()
    }

    companion object {
        const val EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X"
        const val EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y"
    }
}