package com.paraskcd.kcdsearch.services

import android.content.Context
import android.location.Geocoder
import com.paraskcd.kcdsearch.data.repositories.LocationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class LocationService @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val locationRepository: LocationRepository
) {
    private val _cityName = MutableStateFlow<String?>(null)
    val cityName = _cityName.asStateFlow()

    private val _coordinates = MutableStateFlow<Pair<Double, Double>?>(null)
    val coordinates = _coordinates.asStateFlow()

    private val _countryCode = MutableStateFlow<String?>(null)
    val countryCode = _countryCode.asStateFlow()

    fun hasLocationPermission(): Boolean = locationRepository.hasLocationPermission()

    suspend fun loadLocation() {
        if (!hasLocationPermission()) return
        val location = locationRepository.getLastLocation() ?: return
        _coordinates.value = location
        reverseGeocode(location.first, location.second)
    }

    private suspend fun reverseGeocode(lat: Double, lng: Double) =
        suspendCancellableCoroutine { continuation ->
            val geocoder = Geocoder(context, Locale.getDefault())
            geocoder.getFromLocation(lat, lng, 1) { addresses ->
                val address = addresses.firstOrNull()
                _cityName.value = address?.locality
                    ?: address?.subAdminArea
                            ?: address?.adminArea
                _countryCode.value = address?.countryCode
                continuation.resume(Unit)
            }
        }
}