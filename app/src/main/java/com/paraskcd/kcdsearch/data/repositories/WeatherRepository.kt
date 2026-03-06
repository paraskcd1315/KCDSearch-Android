package com.paraskcd.kcdsearch.data.repositories

import com.paraskcd.kcdsearch.data.api.weather.PirateWeatherApi
import com.paraskcd.kcdsearch.data.api.weather.dataSources.PirateWeatherResponse
import com.paraskcd.kcdsearch.di.PirateWeatherApiKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val pirateWeatherApi: PirateWeatherApi,
    @param:PirateWeatherApiKey private val apiKey: String
) {
    suspend fun getForecast(lat: Double, long: Double, units: String = "si"): Result<PirateWeatherResponse> =
        withContext(Dispatchers.IO) {
            if (apiKey.isBlank()) {
                return@withContext Result.failure(
                    IllegalStateException("Pirate Weather API key not configured. Add pirateWeatherApiKey to local.properties.")
                )
            }

            runCatching {
                pirateWeatherApi.getForecast(apiKey, lat, long, units)
            }
        }
}