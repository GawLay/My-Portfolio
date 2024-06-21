package com.kyrie.utility.utility

import android.content.Context
import com.shashank.sony.fancytoastlib.FancyToast

fun Context.showFancyToast(message: String, type: Int =FancyToast.DEFAULT,length:Int = FancyToast.LENGTH_SHORT) {
    FancyToast.makeText(
        this,
        message,
        length,
        type,
        false
    ).show()
}
