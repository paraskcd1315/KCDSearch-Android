package com.paraskcd.kcdsearch.services

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.paraskcd.kcdsearch.data.repositories.SearchRepository
import com.paraskcd.kcdsearch.data.api.search.dataSources.searchResult.SearchResult
import com.paraskcd.kcdsearch.data.api.search.dataSources.infobox.Infobox
import com.paraskcd.kcdsearch.data.api.search.dataSources.searchResult.SearchResultResponse
import com.paraskcd.kcdsearch.data.dtos.SearchRequestDto
import com.paraskcd.kcdsearch.utils.withLoading

@Singleton
class SearchService @Inject constructor(
    private val searchQueryService: SearchQueryService,
    private val searchRepository: SearchRepository,
) {
    private val _results = MutableStateFlow<List<SearchResult>>(emptyList())
    val results = _results.asStateFlow()

    private val _infoboxes = MutableStateFlow<List<Infobox>>(emptyList())
    val infoboxes = _infoboxes.asStateFlow()

    private val _totalResults = MutableStateFlow(0)
    val totalResults = _totalResults.asStateFlow()

    private val _currentPage = MutableStateFlow(1)
    val currentPage = _currentPage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _hasMorePages = MutableStateFlow(true)
    val hasMorePages = _hasMorePages.asStateFlow()

    private val _error = MutableStateFlow<Throwable?>(null)
    val error = _error.asStateFlow()

    suspend fun search(query: String) {
        searchQueryService.setQuery(query)
        resetPagination()
        if (searchQueryService.query.value.isBlank()) return
        withLoading(_isLoading, _error) { fetchPage(1) }.onSuccess { applyPageResponse(it, isFirstPage = true) }
    }

    suspend fun loadNextPage() {
        if (!canLoadMore()) return
        withLoading(_isLoading, _error) { fetchPage(_currentPage.value + 1) }
            .onSuccess { applyPageResponse(it, isFirstPage = false) }
    }

    suspend fun getAutocompleteSuggestions(query: String): List<String> =
        searchRepository.autocomplete(query.trim()).getOrElse { emptyList() }

    fun clear() {
        searchQueryService.clearQuery()
        resetPagination()
    }

    private fun resetPagination() {
        _currentPage.value = 1
        _results.value = emptyList()
        _infoboxes.value = emptyList()
        _totalResults.value = 0
        _hasMorePages.value = true
        _error.value = null
    }

    private suspend fun fetchPage(pageno: Int): Result<SearchResultResponse> =
        searchRepository.search(SearchRequestDto(query = searchQueryService.query.value.trim(), pageno = pageno))

    private fun applyPageResponse(response: SearchResultResponse, isFirstPage: Boolean) {
        if (isFirstPage) {
            _results.value = response.results
            _currentPage.value = 1
        } else {
            _results.value += response.results
            _currentPage.value += 1
        }
        _totalResults.value = response.numberOfResults
        if (response.infoboxes.isNotEmpty()) _infoboxes.value = response.infoboxes
        _hasMorePages.value = response.results.isNotEmpty()
        _error.value = null
    }

    private fun canLoadMore(): Boolean =
        searchQueryService.query.value.isNotBlank() && !_isLoading.value && _hasMorePages.value
}