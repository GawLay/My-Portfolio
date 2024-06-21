package com.kyrie.utility.utility

import android.util.Log

fun Any.showLog(message: Any) = Log.e(this::class.java.name, message.toString())