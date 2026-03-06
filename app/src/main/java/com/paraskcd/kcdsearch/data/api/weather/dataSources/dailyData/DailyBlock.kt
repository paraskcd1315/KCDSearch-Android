package com.paraskcd.kcdsearch.data.api.weather.dataSources.dailyData

data class DailyBlock(
    val summary: String? = null,
    val icon: String? = null,
    val data: List<DailyData> = emptyList()
)
