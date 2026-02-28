package com.paraskcd.kcdsearch.utils.extensionMethods

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun String.toReadableDate(locale: Locale = Locale.getDefault()): String {
    if (isBlank()) return ""
    return try {
        val parsers = listOf(
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ISO_DATE_TIME,
            DateTimeFormatter.ISO_OFFSET_DATE_TIME,
            DateTimeFormatter.ISO_INSTANT
        )
        val formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy · HH:mm", locale)
        val zoned = parsers.mapNotNull { parser ->
            try {
                when {
                    parser === DateTimeFormatter.ISO_INSTANT ->
                        java.time.Instant.parse(this).atZone(ZoneId.systemDefault())
                    parser === DateTimeFormatter.ISO_OFFSET_DATE_TIME ||
                            parser === DateTimeFormatter.ISO_DATE_TIME ->
                        java.time.ZonedDateTime.parse(this, parser)
                    else ->
                        LocalDateTime.parse(this, parser).atZone(ZoneId.systemDefault())
                }
            } catch (_: Exception) { null }
        }.firstOrNull() ?: return this
        formatter.format(zoned)
    } catch (_: Exception) {
        this
    }
}