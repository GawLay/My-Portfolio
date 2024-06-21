package com.kyrie.utility.utility

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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

fun ImageView.getByteArrayFromImageView(): ByteArray {
    val bStream = ByteArrayOutputStream()
    val bitmap = drawable.toBitmap()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream)
    return bStream.toByteArray()
}

fun ByteArray.getBitmap():Bitmap{
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}