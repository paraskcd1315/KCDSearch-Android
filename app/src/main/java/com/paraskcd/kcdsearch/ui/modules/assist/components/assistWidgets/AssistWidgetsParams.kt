package com.paraskcd.kcdsearch.ui.modules.assist.components.assistWidgets

import androidx.compose.ui.graphics.ImageBitmap
import com.paraskcd.kcdsearch.data.api.weather.dataSources.PirateWeatherResponse
import com.paraskcd.kcdsearch.data.local.entities.RecentAppEntity

data class AssistWidgetsParams(
    val forecast: PirateWeatherResponse?,
    val weatherIsLoading: Boolean,
    val cityName: String?,
    val useFahrenheit: Boolean,
    val recentApps: List<RecentAppEntity>,
    val getAppIcon: (String) -> ImageBitmap?,
    val onAppClick: (String) -> Unit,
    val supportsBlur: Boolean,
)
