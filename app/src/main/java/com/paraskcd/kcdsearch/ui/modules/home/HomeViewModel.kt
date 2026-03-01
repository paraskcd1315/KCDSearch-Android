package com.paraskcd.kcdsearch.ui.modules.home

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.kcdsearch.model.SuggestionItem
import com.paraskcd.kcdsearch.services.SearchQueryService
import com.paraskcd.kcdsearch.services.SearchService
import com.paraskcd.kcdsearch.ui.modules.search.SearchActivity
import com.paraskcd.kcdsearch.utils.extensionMethods.toBitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @param:ApplicationContext val context: Context,
    private val packageManager: PackageManager,
    private val searchQueryService: SearchQueryService,
    private val searchService: SearchService
): ViewModel() {
    val query: StateFlow<String> = searchQueryService.query
    val isLoading: StateFlow<Boolean> = searchService.isAutocompleteLoading
    val suggestions: StateFlow<List<SuggestionItem>> = searchService.suggestions

    init {
        viewModelScope.launch {
            onSearchBarExpanded()
        }
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

    fun onSuggestionClick(suggestion: SuggestionItem) {
        when (suggestion) {
            is SuggestionItem.Text -> {
                searchQueryService.setQuery(suggestion.value)
                val intent = Intent(context, SearchActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
            is SuggestionItem.App -> launchApp(suggestion.item.packageName)
        }
    }

    fun getAppIcon(packageName: String): ImageBitmap? = try {
        val drawable = packageManager.getApplicationIcon(packageName)
        drawable.toBitmap().asImageBitmap()
    } catch (e: Exception) {
        null
    }

    private fun launchApp(packageName: String) {
        packageManager.getLaunchIntentForPackage(packageName)?.let { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}