package com.kyrie.utility.animation

import android.app.Activity
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionSet
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator

inline fun transitionTogether(crossinline body: TransitionSet.() -> Unit): TransitionSet {
    return TransitionSet().apply {
        ordering = TransitionSet.ORDERING_TOGETHER
        body()
    }
}
operator fun TransitionSet.plusAssign(transition: Transition) {
    addTransition(transition)
}