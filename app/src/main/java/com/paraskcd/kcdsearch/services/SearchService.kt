package com.paraskcd.kcdsearch.services

import android.util.Log
import com.paraskcd.kcdsearch.data.api.apps.dataSources.AppResult
import com.paraskcd.kcdsearch.data.api.search.dataSources.infobox.Infobox
import com.paraskcd.kcdsearch.data.api.search.dataSources.searchResult.SearchResult
import com.paraskcd.kcdsearch.data.api.search.dataSources.searchResult.SearchResultResponse
import com.paraskcd.kcdsearch.data.dtos.AppSearchRequestDto
import com.paraskcd.kcdsearch.data.dtos.SearchRequestDto
import com.paraskcd.kcdsearch.data.repositories.AppSearchRepository
import com.paraskcd.kcdsearch.data.repositories.SearchRepository
import com.paraskcd.kcdsearch.model.UnifiedSearchResult
import com.paraskcd.kcdsearch.ui.modules.search.enums.SearchCategory
import com.paraskcd.kcdsearch.utils.extensionMethods.toApiString
import com.paraskcd.kcdsearch.utils.globalMethods.withLoading
import com.paraskcd.kcdsearch.utils.globalMethods.withLoadingResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

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

    private val _isAutocompleteLoading = MutableStateFlow(false)
    val isAutocompleteLoading = _isAutocompleteLoading.asStateFlow()

    private val _hasMorePages = MutableStateFlow(true)
    val hasMorePages = _hasMorePages.asStateFlow()

    private val _error = MutableStateFlow<Throwable?>(null)
    val error = _error.asStateFlow()

    private val _autocompleteErrors = MutableStateFlow<Throwable?>(null)
    val autocompleteErrors = _autocompleteErrors.asStateFlow()

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions = _suggestions.asStateFlow()

    private val _category = MutableStateFlow(SearchCategory.General)
    val category = _category.asStateFlow()

    private var suggestionsJob: Job? = null

    fun requestSuggestionsDebounced(scope: CoroutineScope) {
        suggestionsJob?.cancel()
        suggestionsJob = scope.launch {
            delay(300)
            _suggestions.value = getAutocompleteSuggestions()
        }
    }

    suspend fun search() {
        resetPagination()
        val q = searchQueryService.query.value.trim()
        if (q.isBlank()) {
            updateUnifiedResults()
            return
        }
        withLoading(_isLoading, _error) {
            coroutineScope {
                if (_category.value == SearchCategory.General) {
                    launch {
                        Log.d("SearchService", "[APP] starting app search")
                        val appItems = appSearchRepository.search(AppSearchRequestDto(query = q, category = null))
                        Log.d("SearchService", "[APP] completed: ${appItems.size} results")
                        _appResults.value = appItems
                        updateUnifiedResults()
                    }
                }
                launch {
                    Log.d("SearchService", "[WEB] starting web search")
                    val webResult = searchRepository.search(SearchRequestDto(query = q, pageno = 1, categories = _category.value.toApiString()))
                    Log.d("SearchService", "[WEB] completed: success=${webResult.isSuccess}, ${webResult.getOrNull()?.results?.size ?: 0} results")
                    webResult.onSuccess {
                        applyWebPageResponse(it, isFirstPage = true)
                        Log.d("SearchService", "[WEB] applied: _webResults.size=${_webResults.value.size}")
                    }
                    webResult.onFailure {
                        _error.value = it
                        Log.e("SearchService", "[WEB] failed", it)
                    }
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
                SearchRequestDto(
                    query = searchQueryService.query.value.trim(),
                    pageno = _currentPage.value + 1,
                    categories = _category.value.toApiString()
                ),
            )
            webResult.onSuccess { applyWebPageResponse(it, isFirstPage = false) }
            webResult.onFailure { _error.value = it }
            updateUnifiedResults()
        }
    }

    suspend fun getAutocompleteSuggestions(): List<String> {
        val query = searchQueryService.query.value.trim()
        if (query.length <= 2) return emptyList()
        return withLoadingResult(_isAutocompleteLoading, _autocompleteErrors) {
            searchRepository.autocomplete(query).getOrElse { emptyList() }
        }
    }

    fun clear() {
        searchQueryService.clearQuery()
        _category.value = SearchCategory.General
        resetPagination()
    }

    fun setCategory(category: SearchCategory) {
        _category.value = category
    }

    fun clearError() {
        _error.value = null
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

        if (_category.value == SearchCategory.General && response.infoboxes.isNotEmpty()) {
            _infoboxes.value = response.infoboxes
            Log.d("Infoboxes", response.infoboxes.toString())
        } else if (_category.value != SearchCategory.General) {
            _infoboxes.value = emptyList()
        }

        _hasMorePages.value = response.results.isNotEmpty()
        _error.value = null
    }

    private fun updateUnifiedResults() {
        val appCount = _appResults.value.size
        val webCount = _webResults.value.size
        _results.value = _appResults.value.map { UnifiedSearchResult.App(it) } + _webResults.value.map { UnifiedSearchResult.Web(it) }
        Log.d("SearchService", "updateUnifiedResults: app=$appCount, web=$webCount, total=${_results.value.size}")
    }

    private fun canLoadMore(): Boolean =
        searchQueryService.query.value.isNotBlank() && !_isLoading.value && _hasMorePages.value
}