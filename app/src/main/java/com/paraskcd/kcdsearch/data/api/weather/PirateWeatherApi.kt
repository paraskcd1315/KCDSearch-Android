package com.paraskcd.kcdsearch.data.api.weather

import com.paraskcd.kcdsearch.data.api.weather.dataSources.PirateWeatherResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PirateWeatherApi {
    @GET("forecast/{apiKey}/{lat},{long}")
    suspend fun getForecast(
        @Path("apiKey") apiKey: String,
        @Path("lat") lat: Double,
        @Path("long") long: Double,
        @Query("units") units: String = "si",
        @Query("exclude") exclude: String = "hourly,daily,minutely,flags",
        @Query("lang") lang: String = "en",
        @Query("icon") icon: String = "pirate"
    ): PirateWeatherResponse
}