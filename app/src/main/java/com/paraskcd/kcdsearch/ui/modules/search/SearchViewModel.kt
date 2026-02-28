package com.paraskcd.kcdsearch.ui.modules.search

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.kcdsearch.services.SearchQueryService
import com.paraskcd.kcdsearch.services.SearchService
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
    val query: StateFlow<String> = searchQueryService.query
    val results = searchService.results
    val isLoading = searchService.isLoading
    val errors = searchService.error
    val infoboxes = searchService.infoboxes
    val totalResults = searchService.totalResults
    val hasMorePages = searchService.hasMorePages
    val isSuggestionsLoading: StateFlow<Boolean> = searchService.isAutocompleteLoading
    val suggestions: StateFlow<List<String>> = searchService.suggestions

    private var suggestionsJob: Job? = null
    private val iconCache = mutableMapOf<String, androidx.compose.ui.graphics.ImageBitmap?>()

    init {
        viewModelScope.launch {
            searchService.search()
        }
    }

    fun setQuery(value: String) {
        searchQueryService.setQuery(value)
        searchService.requestSuggestionsDebounced(viewModelScope)
    }

    fun loadNextPage() {
        viewModelScope.launch {
            searchService.loadNextPage()
        }
    }

    fun clearQuery() {
        searchService.clear()
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
}