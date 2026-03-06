package com.paraskcd.kcdsearch.utils.extensionMethods

import kotlin.math.roundToInt

fun Double?.toTempString(useFahrenheit: Boolean = false): String {
    if (this == null) return "--°"
    val unit = if (useFahrenheit) "F" else "C"
    return "${this.roundToInt()}°$unit"
}
