package com.paraskcd.kcdsearch.ui.modules.home.components.weatherWidget

import androidx.compose.ui.Modifier
import com.paraskcd.kcdsearch.data.api.weather.dataSources.PirateWeatherResponse

data class WeatherWidgetParams(
    val isLoading: Boolean,
    val error: Throwable?,
    val forecast: PirateWeatherResponse?,
    val cityName: String?,
    val useFahrenheit: Boolean,
    val requiresPermission: Boolean,
    val onRequestPermission: () -> Unit,
    val onRetry: () -> Unit,
    val modifier: Modifier = Modifier
)

