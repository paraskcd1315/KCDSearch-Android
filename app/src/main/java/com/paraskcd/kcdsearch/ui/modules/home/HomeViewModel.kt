package com.paraskcd.kcdsearch.ui.modules.home

import android.util.Log
import com.paraskcd.kcdsearch.services.SearchService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.kcdsearch.services.SearchQueryService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val searchQueryService: SearchQueryService,
    private val searchService: SearchService
): ViewModel() {
    val query: StateFlow<String> = searchQueryService.query
    val isLoading: StateFlow<Boolean> = searchService.isAutocompleteLoading

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()

    private var suggestionsJob: Job? = null

    fun setQuery(value: String) {
        searchQueryService.setQuery(value)
        loadSuggestionsDebounced()
    }

    private fun loadSuggestionsDebounced() {
        suggestionsJob?.cancel()
        suggestionsJob = viewModelScope.launch {
            delay(300)
            _suggestions.value = searchService.getAutocompleteSuggestions()
            Log.d("Suggestions", suggestions.value.toString())
        }
    }
}