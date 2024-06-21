package com.kyrie.utility.utility

import android.app.Activity
import android.os.Build
import android.view.*

fun Activity.awaitViewDraw(onPreDraw:()->Unit){
    val decor = window.decorView
    decor.viewTreeObserver.addOnPreDrawListener(object :
        ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            decor.viewTreeObserver.removeOnPreDrawListener(this)
            onPreDraw.invoke()
            return true
        }
    })
}

//fun Activity.hideStatusBar(){
////    requestWindowFeature(Window.FEATURE_NO_TITLE);
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//        window.insetsController?.hide(WindowInsets.Type.statusBars())
//    } else {
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
//    }
//}

fun Activity.changeStatusBarColor(color:Int){
    val window: Window = window
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = color

}

 fun Activity.hideStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.hide(WindowInsets.Type.statusBars())
    } else {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                )
    }
}

 fun Activity.showStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.show(WindowInsets.Type.statusBars())
    } else {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_VISIBLE
                )
    }
}