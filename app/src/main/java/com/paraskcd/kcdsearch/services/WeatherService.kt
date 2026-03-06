package com.paraskcd.kcdsearch.services

import com.paraskcd.kcdsearch.data.api.weather.dataSources.PirateWeatherResponse
import com.paraskcd.kcdsearch.data.repositories.WeatherRepository
import com.paraskcd.kcdsearch.utils.globalMethods.withLoading
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherService @Inject constructor(
    private val locationService: LocationService,
    private val weatherRepository: WeatherRepository
) {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<Throwable?>(null)
    val error = _error.asStateFlow()

    private val _forecast = MutableStateFlow<PirateWeatherResponse?>(null)
    val forecast = _forecast.asStateFlow()

    private val _requiresPermission = MutableStateFlow(false)
    val requiresPermission = _requiresPermission.asStateFlow()

    private val _useFahrenheit = MutableStateFlow(false)
    val useFahrenheit = _useFahrenheit.asStateFlow()


    val cityName: StateFlow<String?> = locationService.cityName

    fun hasLocationPermission(): Boolean = locationService.hasLocationPermission()

    suspend fun loadForecast() {
        if (!locationService.hasLocationPermission()) {
            _requiresPermission.value = true
            return
        }
        _requiresPermission.value = false
        withLoading(_isLoading, _error) {
            locationService.loadLocation()
            val coordinates = locationService.coordinates.value
                ?: throw IllegalStateException("Unable to determine your location.")
            val isFahrenheit = locationService.countryCode.value == "US"
            _useFahrenheit.value = isFahrenheit
            val units = if (isFahrenheit) "us" else "si"
            weatherRepository.getForecast(coordinates.first, coordinates.second, units).fold(
                onSuccess = { _forecast.value = it },
                onFailure = { throw it }
            )
        }
    }


    fun clearError() {
        _error.value = null
    }
}