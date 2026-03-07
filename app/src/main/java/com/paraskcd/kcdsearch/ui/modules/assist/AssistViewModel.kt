package com.paraskcd.kcdsearch.ui.modules.assist

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.net.toUri
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.kcdsearch.model.SuggestionItem
import com.paraskcd.kcdsearch.data.local.entities.RecentAppEntity
import com.paraskcd.kcdsearch.data.repositories.RecentAppsRepository
import com.paraskcd.kcdsearch.services.SearchQueryService
import com.paraskcd.kcdsearch.services.SearchService
import com.paraskcd.kcdsearch.services.WeatherService
import com.paraskcd.kcdsearch.ui.modules.search.SearchActivity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import com.paraskcd.kcdsearch.utils.extensionMethods.toBitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssistViewModel @Inject constructor(
    @param:ApplicationContext val context: Context,
    private val packageManager: PackageManager,
    private val searchQueryService: SearchQueryService,
    private val searchService: SearchService,
    private val weatherService: WeatherService,
    private val recentAppsRepository: RecentAppsRepository
): ViewModel() {
    val query = searchQueryService.query
    val isLoading = searchService.isAutocompleteLoading
    val suggestions = searchService.suggestions
    val autocompleteErrors = searchService.autocompleteErrors

    val weatherForecast = weatherService.forecast
    val weatherIsLoading = weatherService.isLoading
    val weatherCityName = weatherService.cityName
    val useFahrenheit = weatherService.useFahrenheit
    val weatherRequiresPermission = weatherService.requiresPermission

    val recentApps = recentAppsRepository.getMostUsedApps(8)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            loadRecentSearches()
            weatherService.loadForecast()
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
            searchQueryService.clearQuery()
            searchService.requestSuggestionsImmediate(viewModelScope)
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
        }
    }

    fun getAppIcon(packageName: String): ImageBitmap? = try {
        val drawable = packageManager.getApplicationIcon(packageName)
        drawable.toBitmap().asImageBitmap()
    } catch (e: Exception) {
        null
    }

    fun onClear() {
        searchService.clear(viewModelScope)
    }

    fun isWhatsappInstalled(): Boolean = searchService.isWhatsappInstalled()

    fun openDialer(number: String) {
        val intent = Intent(Intent.ACTION_DIAL, "tel:$number".toUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun openMessages(number: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "smsto:$number".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun openWhatsApp(number: String) {
        val sanitized = number.replace("[^\\d+]".toRegex(), "")
        val intent = Intent(Intent.ACTION_VIEW, "https://wa.me/$sanitized".toUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun launchAppFromWidget(packageName: String) {
        launchApp(packageName)
        viewModelScope.launch {
            val label = try {
                packageManager.getApplicationInfo(packageName, 0)
                    .let { packageManager.getApplicationLabel(it).toString() }
            } catch (_: Exception) { packageName }
            recentAppsRepository.recordAppLaunch(packageName, label)
        }
    }

    private fun launchApp(packageName: String) {
        packageManager.getLaunchIntentForPackage(packageName)?.let { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}