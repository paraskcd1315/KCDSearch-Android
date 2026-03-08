package com.paraskcd.kcdsearch.ui.activities.home.components.weatherWidget.contents.weatherErrorContent

import androidx.compose.ui.Modifier

data class WeatherErrorContentParams(
    val message: String?,
    val onRetry: () -> Unit,
    val modifier: Modifier = Modifier
)
