package com.paraskcd.kcdsearch.data.api.weather.dataSources.dailyData

import com.google.gson.annotations.SerializedName

data class DailyData(
    val time: Long,
    val summary: String? = null,
    val icon: String? = null,
    @SerializedName("temperatureHigh")
    val temperatureHigh: Double? = null,
    @SerializedName("temperatureLow")
    val temperatureLow: Double? = null,
    val sunriseTime: Long? = null,
    val sunsetTime: Long? = null,
    val precipProbability: Double? = null,
    val precipType: String? = null
)
