package com.paraskcd.kcdsearch.data.api.weather.dataSources

import com.paraskcd.kcdsearch.data.api.weather.dataSources.dailyData.DailyBlock

data class PirateWeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String? = null,
    val offset: Double? = null,
    val currently: CurrentlyData? = null,
    val daily: DailyBlock? = null,
    val flags: Flags? = null
)
