package com.paraskcd.kcdsearch.utils.extensionMethods

import android.content.Context
import android.os.PowerManager
import android.view.WindowManager

fun Context.isWindowBlurSupported(): Boolean {
    val wm = getSystemService(WindowManager::class.java) ?: return false
    return wm.isCrossWindowBlurEnabled
}

fun Context.isBatterySaverOn(): Boolean {
    val pm = getSystemService(PowerManager::class.java) ?: return false
    return pm.isPowerSaveMode
}
