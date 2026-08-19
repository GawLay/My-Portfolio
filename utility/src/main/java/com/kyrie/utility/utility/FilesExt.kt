package com.kyrie.utility.utility

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import java.io.ByteArrayOutputStream

fun ImageView.getBitmapFromImage(): Bitmap {
    val bitmap = drawable.toBitmap()
    return bitmap
}

fun Bitmap.getByteArrayFromBitmap(): ByteArray {
    val bStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 100, bStream)
    return bStream.toByteArray()
}
