package com.kyrie.utility.animation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.util.Property
import android.view.View
import com.kyrie.utility.simplifyInteractors.AnimatorListenerAdapter

val ALPHA: Property<View, Float> = View.ALPHA
val ROTATE:Property<View,Float> = View.ROTATION
val TRANSLATION_Y: Property<View, Float> = View.TRANSLATION_Y
val TRANSLATION_X: Property<View, Float> = View.TRANSLATION_X
val SCALE_X: Property<View, Float> = View.SCALE_X
val SCALE_Y: Property<View, Float> = View.SCALE_Y
const val ONE_SECOND = 1000L
const val HALF_SECOND = 500L

fun View.createAnim(
    property: Property<View, Float>,
    startValue: Float,
    endValue: Float,
    duration: Long = ONE_SECOND
): ObjectAnimator {
    return ObjectAnimator.ofFloat(
        this,
        property,
        startValue,
        endValue
    ).apply {
        this.duration = duration
    }
}

fun ObjectAnimator.makeInfiniteAndReverse(mode: Int = ObjectAnimator.REVERSE): ObjectAnimator {
    return this.apply {
        repeatCount = ObjectAnimator.INFINITE
        repeatMode = mode
    }
}


fun startAnimSet(vararg animations: Animator, listener: (Animator) -> Unit = {}) {
    val animationSet = AnimatorSet()
    for (animation in animations) {
        animationSet.playTogether(animation)
    }
    animationSet.start()
    animationSet.addListener(AnimatorListenerAdapter(
        onEnd = {
            listener(it)
        }
    ))
}

fun getAnimSet(vararg animations: ObjectAnimator):AnimatorSet {
    val animationSet = AnimatorSet()
    for (animation in animations) {
        animationSet.playTogether(animation)
    }
    return animationSet
}
