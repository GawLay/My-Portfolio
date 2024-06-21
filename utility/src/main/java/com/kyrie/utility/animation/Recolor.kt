package com.kyrie.utility.animation

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.transition.Transition
import android.transition.TransitionValues
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView

class Recolor : Transition {
    constructor() {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    companion object {
        private const val PROPNAME_BACKGROUND = "android:recolor:background"
    }
    private fun captureValues(transitionValues: TransitionValues) {
        transitionValues.values[PROPNAME_BACKGROUND] = transitionValues.view.background
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun createAnimator(
        sceneRoot: ViewGroup, startValues: TransitionValues,
        endValues: TransitionValues
    ): Animator? {
        val view = endValues.view
        val startBackground = startValues.values[PROPNAME_BACKGROUND] as Drawable
        val endBackground = endValues.values[PROPNAME_BACKGROUND] as Drawable
        if (startBackground is ColorDrawable && endBackground is ColorDrawable) {
            val startColor = startBackground
            val endColor = endBackground
            if (startColor.color != endColor.color) {
                endColor.color = startColor.color
                return ObjectAnimator.ofObject(
                    endBackground!!, "color",
                    ArgbEvaluator(), startColor.color, endColor.color
                )
            }
        }
        return null
    }

}