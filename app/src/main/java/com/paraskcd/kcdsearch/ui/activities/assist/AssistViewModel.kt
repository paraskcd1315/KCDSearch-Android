package com.paraskcd.kcdsearch.ui.activities.assist

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.net.toUri
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
class AssistViewModel @Inject constructor(
    @param:ApplicationContext val context: Context,
    private val packageManager: PackageManager,
    private val searchQueryService: SearchQueryService,
    private val searchService: SearchService,
    private val weatherService: WeatherService,
    private val quickSearchRepository: QuickSearchRepository
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

    init {
        viewModelScope.launch {
            weatherService.loadForecast()
        }
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
            is SuggestionItem.Contact -> {
                val uri = searchService.getContactUriByNumber(suggestion.item.number)
                openContactDetails(uri)
            }
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

    fun isWhatsappInstalled(): Boolean = searchService.isWhatsappInstalled()

    private fun openContactDetails(uri: Uri?) {
        uri?.let {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = it
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private fun launchApp(packageName: String) {
        packageManager.getLaunchIntentForPackage(packageName)?.let { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}