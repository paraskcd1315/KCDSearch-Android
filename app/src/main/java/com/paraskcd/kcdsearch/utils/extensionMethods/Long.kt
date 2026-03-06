package com.paraskcd.kcdsearch.utils.extensionMethods

import java.time.Instant
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

fun Long.toDayLabel(): String {
    val localDate = Instant.ofEpochSecond(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    return localDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
}