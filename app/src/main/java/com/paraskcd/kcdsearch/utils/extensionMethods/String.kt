package com.paraskcd.kcdsearch.utils.extensionMethods

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.icons.lucide.Cloud
import com.composables.icons.lucide.CloudDrizzle
import com.composables.icons.lucide.CloudFog
import com.composables.icons.lucide.CloudRain
import com.composables.icons.lucide.CloudSnow
import com.composables.icons.lucide.CloudSun
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Moon
import com.composables.icons.lucide.Sun
import com.composables.icons.lucide.Wind
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

fun String.normalizePhoneNumber(): String = replace(Regex("[^0-9]"), "")

fun String?.toWeatherIcon(): ImageVector = when (this) {
    "clear-day"           -> Lucide.Sun
    "clear-night"         -> Lucide.Moon
    "partly-cloudy-day"   -> Lucide.CloudSun
    "partly-cloudy-night" -> Lucide.Cloud
    "cloudy"              -> Lucide.Cloud
    "rain"                -> Lucide.CloudRain
    "sleet"               -> Lucide.CloudDrizzle
    "snow"                -> Lucide.CloudSnow
    "wind"                -> Lucide.Wind
    "fog"                 -> Lucide.CloudFog
    else                  -> Lucide.Sun
}

fun String?.toWeatherDescription(): String = when (this) {
    "clear-day" -> "Clear"
    "clear-night" -> "Clear Night"
    "rain" -> "Rain"
    "snow" -> "Snow"
    "sleet" -> "Sleet"
    "wind" -> "Windy"
    "fog" -> "Fog"
    "cloudy" -> "Cloudy"
    "partly-cloudy-day" -> "Partly Cloudy"
    "partly-cloudy-night" -> "Partly Cloudy"
    "hail" -> "Hail"
    "thunderstorm" -> "Thunderstorm"
    "tornado" -> "Tornado"
    "drizzle" -> "Drizzle"
    else -> "Unknown"
}
