package com.paraskcd.kcdsearch.ui.modules.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.kcdsearch.services.SearchQueryService
import com.paraskcd.kcdsearch.services.SearchService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
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

    init {
        viewModelScope.launch {
            searchService.search()
        }
    }

    fun loadNextPage() {
        viewModelScope.launch {
            searchService.loadNextPage()
        }
    }
}