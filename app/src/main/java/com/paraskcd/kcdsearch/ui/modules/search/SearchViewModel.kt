package com.paraskcd.kcdsearch.ui.modules.search

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.kcdsearch.model.SuggestionItem
import com.paraskcd.kcdsearch.services.SearchQueryService
import com.paraskcd.kcdsearch.services.SearchService
import com.paraskcd.kcdsearch.ui.modules.search.enums.SearchCategory
import com.paraskcd.kcdsearch.utils.extensionMethods.toBitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val packageManager: PackageManager,
    private val searchService: SearchService,
    private val searchQueryService: SearchQueryService
): ViewModel() {
    val query = searchQueryService.query
    val category = searchService.category
    val results = searchService.results
    val isLoading = searchService.isLoading
    val errors = searchService.error
    val autocompleteErrors = searchService.autocompleteErrors
    val infoboxes = searchService.infoboxes
    val totalResults = searchService.totalResults
    val hasMorePages = searchService.hasMorePages
    val isSuggestionsLoading = searchService.isAutocompleteLoading
    val suggestions = searchService.suggestions
    private val iconCache = mutableMapOf<String, androidx.compose.ui.graphics.ImageBitmap?>()

    init {
        viewModelScope.launch {
            searchService.search()
        }
    }

    fun onSearchBarExpanded() {
        searchService.requestSuggestionsImmediate(viewModelScope)
    }

    fun clearAutocompleteError() {
        searchService.clearAutocompleteError()
    }

    fun setQuery(value: String) {
        if (value.isBlank()) {
            searchService.clear(viewModelScope)
            return
        }
        searchQueryService.setQuery(value)
        searchService.requestSuggestionsDebounced(viewModelScope)
    }

    fun submitSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return
        searchQueryService.setQuery(trimmed)
        viewModelScope.launch {
            searchService.search()
        }
    }

    fun contactRequiresPermission(): Boolean = searchService.contactRequiresPermission()

    fun isWhatsappInstalled(): Boolean = searchService.isWhatsappInstalled()

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

    fun onSuggestionClick(suggestion: SuggestionItem) {
        when (suggestion) {
            is SuggestionItem.Text -> submitSearch(suggestion.value)
            is SuggestionItem.App -> launchApp(suggestion.item.packageName)
            is SuggestionItem.Contact -> openContactDetails(searchService.getContactUriByNumber(suggestion.item.number))
        }
    }

    fun clearError() {
        searchService.clearError()
    }

    fun loadNextPage() {
        viewModelScope.launch {
            searchService.loadNextPage()
        }
    }

    fun clearQuery() {
        searchService.clear(viewModelScope)
    }

    fun getAppIcon(packageName: String): ImageBitmap? {
        if (iconCache.containsKey(packageName)) return iconCache[packageName]
        return try {
            val drawable = packageManager.getApplicationIcon(packageName)
            val bitmap = drawable.toBitmap().asImageBitmap()
            iconCache[packageName] = bitmap
            bitmap
        } catch (e: Exception) {
            iconCache[packageName] = null
            null
        }
    }

    fun launchApp(packageName: String) {
        packageManager.getLaunchIntentForPackage(packageName)?.let { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            // no browser available
        }
    }

    fun setCategoryAndSearch(category: SearchCategory) {
        searchService.setCategory(category)
        viewModelScope.launch {
            searchService.search()
        }
    }
}