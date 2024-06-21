package com.kyrie.utility.animation

import android.animation.ValueAnimator
import android.content.Context

fun Context.createValueAnimatorAnim(
    fromColor: Int, toColor: Int,
    duration: Long, updateListener: ValueAnimator.() -> Unit
) {
    val animator = ValueAnimator.ofArgb(
        fromColor, toColor
    )
    animator.duration = duration
    animator.addUpdateListener { animation ->
        updateListener.invoke(animation)
    }
    animator.start()
}