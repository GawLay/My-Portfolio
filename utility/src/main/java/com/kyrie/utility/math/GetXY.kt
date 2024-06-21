package com.kyrie.utility.math

import android.app.Activity
import android.graphics.Point
import android.view.View

object GetXY {
    fun getCenterValueOfView(view: View?): Map<String, Int> {

        val cx = (view!!.x + view.width / 2).toInt()
        val cy = (view.y + view.height / 2).toInt()
        return mapOf("cx" to cx, "cy" to cy)
    }

    fun getCenterValueOfViewOnWindow(view: View): IntArray {
        val viewLocation = IntArray(2)
        view.getLocationInWindow(viewLocation)
        return viewLocation
    }

    fun getCenterValueOfScreen(activity: Activity): Point {
        val cx = activity.resources.displayMetrics.widthPixels / 2
        val cy = activity.resources.displayMetrics.heightPixels / 2
        return Point(cx, cy)
    }

}