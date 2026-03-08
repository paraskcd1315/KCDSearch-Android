package com.paraskcd.kcdsearch.ui.activities.home

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.kcdsearch.data.repositories.QuickSearchRepository
import com.paraskcd.kcdsearch.model.SuggestionItem
import com.paraskcd.kcdsearch.services.SearchQueryService
import com.paraskcd.kcdsearch.services.SearchService
import com.paraskcd.kcdsearch.services.WeatherService
import com.paraskcd.kcdsearch.ui.activities.search.SearchActivity
import com.paraskcd.kcdsearch.utils.extensionMethods.toBitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @param:ApplicationContext val context: Context,
    private val packageManager: PackageManager,
    private val searchQueryService: SearchQueryService,
    private val searchService: SearchService,
    private val weatherService: WeatherService,
    private val quickSearchRepository: QuickSearchRepository
): ViewModel() {
    val query = searchQueryService.query
    val isLoading = searchService.isAutocompleteLoading
    val isLoadingResults = searchService.isLoading
    val results = searchService.results
    val suggestions = searchService.suggestions
    val autocompleteErrors = searchService.autocompleteErrors
    val recentSearches = searchService.recentSearches

    val weatherIsLoading = weatherService.isLoading
    val weatherError = weatherService.error
    val weatherForecast = weatherService.forecast
    val weatherRequiresPermission = weatherService.requiresPermission
    val weatherCityName = weatherService.cityName
    val useFahrenheit = weatherService.useFahrenheit

    init {
        viewModelScope.launch {
            onSearchBarExpanded()
            loadRecentSearches()
            loadWeather()
        }
    }

    fun loadRecentSearches() {
        searchService.loadRecentSearches(5, viewModelScope)
    }

    fun onSearchBarExpanded() {
        searchService.requestSuggestionsImmediate(viewModelScope)
    }

    fun setQuery(value: String) {
        if (value.isBlank()) {
            searchService.clear(viewModelScope)
            return
        }
        searchQueryService.setQuery(value)
        searchService.requestSuggestionsDebounced(viewModelScope)
    }

    fun clearAutocompleteError() {
        searchService.clearAutocompleteError()
    }

    fun getContactUriByNumber(number: String) = searchService.getContactUriByNumber(number)

    fun openContactDetails(uri: Uri?) {
        uri?.let {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = it
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    fun onSuggestionClick(suggestion: SuggestionItem) {
        when (suggestion) {
            is SuggestionItem.Text -> {
                searchQueryService.setQuery(suggestion.value)
                val intent = Intent(context, SearchActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
            is SuggestionItem.App -> {
                launchApp(suggestion.item.packageName)
                searchService.clear(viewModelScope)
            }
            is SuggestionItem.Contact -> openContactDetails(getContactUriByNumber(suggestion.item.number))
            is SuggestionItem.SearchAction -> {
                searchQueryService.setQuery(suggestion.item.query)
                val intent = quickSearchRepository.buildIntent(suggestion.item).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        }
    }

    fun getAppIcon(packageName: String): ImageBitmap? = try {
        val drawable = packageManager.getApplicationIcon(packageName)
        drawable.toBitmap().asImageBitmap()
    } catch (e: Exception) {
        null
    }

    fun onSearchbarCollapse() {
        if (isLoadingResults.value || results.value.isNotEmpty()) {
            searchService.clear(viewModelScope)
        }
    }

    suspend fun loadWeather() {
        weatherService.loadForecast()
    }

    fun onLocationPermissionResult(granted: Boolean) {
        viewModelScope.launch {
            if (granted) {
                loadWeather()
            }
        }
    }

    fun deleteRecentSearch(query: String) {
        searchService.deleteRecentSearch(query, 5, viewModelScope)
    }

    fun restoreRecentSearch(query: String) {
        searchService.restoreRecentSearch(query, 5, viewModelScope)
    }

    fun clearWeatherError() {
        weatherService.clearError()
    }

    private fun launchApp(packageName: String) {
        packageManager.getLaunchIntentForPackage(packageName)?.let { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}