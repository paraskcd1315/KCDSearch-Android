package com.paraskcd.kcdsearch.services

import android.util.Log
import com.paraskcd.kcdsearch.data.api.apps.dataSources.AppResult
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.paraskcd.kcdsearch.data.repositories.SearchRepository
import com.paraskcd.kcdsearch.data.api.search.dataSources.searchResult.SearchResult
import com.paraskcd.kcdsearch.data.api.search.dataSources.infobox.Infobox
import com.paraskcd.kcdsearch.data.api.search.dataSources.searchResult.SearchResultResponse
import com.paraskcd.kcdsearch.data.dtos.AppSearchRequestDto
import com.paraskcd.kcdsearch.data.dtos.SearchRequestDto
import com.paraskcd.kcdsearch.data.repositories.AppSearchRepository
import com.paraskcd.kcdsearch.model.UnifiedSearchResult
import com.paraskcd.kcdsearch.utils.withLoading
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Singleton
class SearchService @Inject constructor(
    private val searchQueryService: SearchQueryService,
    private val searchRepository: SearchRepository,
    private val appSearchRepository: AppSearchRepository,
) {
    private val _webResults = MutableStateFlow<List<SearchResult>>(emptyList())
    private val _appResults = MutableStateFlow<List<AppResult>>(emptyList())

    private val _results = MutableStateFlow<List<UnifiedSearchResult>>(emptyList())
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
        val q = searchQueryService.query.value.trim()
        if (q.isBlank()) {
            updateUnifiedResults()
            return
        }
        withLoading(_isLoading, _error) {
            coroutineScope {
                launch {
                    val appItems = appSearchRepository.search(AppSearchRequestDto(query = q, category = null))
                    _appResults.value = appItems
                    updateUnifiedResults()
                }
                launch {
                    val webResult = searchRepository.search(SearchRequestDto(query = q, pageno = 1))
                    webResult.onSuccess { applyWebPageResponse(it, isFirstPage = true) }
                    webResult.onFailure { _error.value = it }
                    updateUnifiedResults()
                }
            }
        }
    }

    suspend fun loadNextPage() {
        if (!canLoadMore()) {
            return
        }
        withLoading(_isLoading, _error) {
            val webResult = searchRepository.search(
                SearchRequestDto(query = searchQueryService.query.value.trim(), pageno = _currentPage.value + 1),
            )
            webResult.onSuccess { applyWebPageResponse(it, isFirstPage = false) }
            webResult.onFailure { _error.value = it }
            updateUnifiedResults()
        }
    }

    suspend fun getAutocompleteSuggestions(): List<String> {
        val query = searchQueryService.query.value.trim()
        if (query.length <= 2) {
            return emptyList()
        }

        Log.d("SuggestionsService", query)

        return searchRepository.autocomplete(searchQueryService.query.value.trim()).getOrElse { emptyList() }
    }

    fun clear() {
        searchQueryService.clearQuery()
        resetPagination()
    }

    private fun resetPagination() {
        _currentPage.value = 1
        _webResults.value = emptyList()
        _appResults.value = emptyList()
        _infoboxes.value = emptyList()
        _totalResults.value = 0
        _hasMorePages.value = true
        _error.value = null

        updateUnifiedResults()
    }

    private fun applyWebPageResponse(response: SearchResultResponse, isFirstPage: Boolean) {
        if (isFirstPage) {
            _webResults.value = response.results
            _currentPage.value = 1
        } else {
            _webResults.value = _webResults.value + response.results
            _currentPage.value = _currentPage.value + 1
        }

        _totalResults.value = response.numberOfResults

        if (response.infoboxes.isNotEmpty()) {
            _infoboxes.value = response.infoboxes
        }

        _hasMorePages.value = response.results.isNotEmpty()
        _error.value = null
    }

    private fun updateUnifiedResults() {
        _results.value = _appResults.value.map { UnifiedSearchResult.App(it) } + _webResults.value.map { UnifiedSearchResult.Web(it) }
    }

    private fun canLoadMore(): Boolean =
        searchQueryService.query.value.isNotBlank() && !_isLoading.value && _hasMorePages.value
}