package com.paraskcd.kcdsearch.data.api.weather.dataSources

data class CurrentlyData(
    val time: Long,
    val summary: String? = null,
    val icon: String? = null,
    val temperature: Double? = null,
    val apparentTemperature: Double? = null,
    val humidity: Double? = null,
    val windSpeed: Double? = null,
    val windGust: Double? = null,
    val precipProbability: Double? = null,
    val precipType: String? = null,
    val cloudCover: Double? = null,
    val visibility: Double? = null,
    val pressure: Double? = null
)
