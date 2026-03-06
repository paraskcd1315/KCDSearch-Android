package com.paraskcd.kcdsearch.ui.modules.home.components.weatherWidget.contents.weatherContent

import androidx.compose.ui.Modifier
import com.paraskcd.kcdsearch.data.api.weather.dataSources.PirateWeatherResponse

data class WeatherContentParams(
    val forecast: PirateWeatherResponse?,
    val cityName: String?,
    val modifier: Modifier = Modifier,
    val useFahrenheit: Boolean
)
