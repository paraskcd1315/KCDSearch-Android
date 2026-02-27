package com.paraskcd.kcdsearch.ui.modules.home

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.kcdsearch.services.SearchQueryService
import com.paraskcd.kcdsearch.services.SearchService
import com.paraskcd.kcdsearch.ui.modules.search.SearchActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @param:ApplicationContext val context: Context,
    private val searchQueryService: SearchQueryService,
    private val searchService: SearchService
): ViewModel() {
    val query: StateFlow<String> = searchQueryService.query
    val isLoading: StateFlow<Boolean> = searchService.isAutocompleteLoading
    val suggestions: StateFlow<List<String>> = searchService.suggestions

    fun setQuery(value: String) {
        searchQueryService.setQuery(value)
        searchService.requestSuggestionsDebounced(viewModelScope)
    }

    fun onSuggestionClick(suggestion: String) {
        searchQueryService.setQuery(suggestion)
        val intent = Intent(context, SearchActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}