package com.paraskcd.kcdsearch.utils.extensionMethods

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.createBitmap

fun Drawable.toBitmap(): Bitmap {
    return if (this is BitmapDrawable) bitmap
    else {
        val bitmap = createBitmap(48, 48)
        val canvas = Canvas(bitmap)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
        bitmap
    }
}