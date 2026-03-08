package com.paraskcd.kcdsearch.utils.extensionMethods

import android.content.Context
import android.view.WindowManager

fun Context.isWindowBlurSupported(): Boolean =
    getSystemService(WindowManager::class.java)
                ?.isCrossWindowBlurEnabled == true