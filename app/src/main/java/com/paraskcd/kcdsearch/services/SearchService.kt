package com.paraskcd.kcdsearch.services

import android.util.Log
import com.paraskcd.kcdsearch.data.api.apps.dataSources.AppResult
import com.paraskcd.kcdsearch.data.api.contacts.dataSources.ContactResult
import com.paraskcd.kcdsearch.data.api.search.dataSources.infobox.Infobox
import com.paraskcd.kcdsearch.data.api.search.dataSources.searchResult.SearchResult
import com.paraskcd.kcdsearch.data.api.search.dataSources.searchResult.SearchResultResponse
import com.paraskcd.kcdsearch.data.dtos.AppSearchRequestDto
import com.paraskcd.kcdsearch.data.dtos.ContactSearchRequestDto
import com.paraskcd.kcdsearch.data.dtos.SearchRequestDto
import com.paraskcd.kcdsearch.data.repositories.AppSearchRepository
import com.paraskcd.kcdsearch.data.repositories.ContactSearchRepository
import com.paraskcd.kcdsearch.data.repositories.QuickSearchRepository
import com.paraskcd.kcdsearch.data.repositories.SearchHistoryRepository
import com.paraskcd.kcdsearch.data.repositories.SearchRepository
import com.paraskcd.kcdsearch.model.SuggestionItem
import com.paraskcd.kcdsearch.model.UnifiedSearchResult
import com.paraskcd.kcdsearch.ui.activities.search.enums.SearchCategory
import com.paraskcd.kcdsearch.utils.extensionMethods.toApiString
import com.paraskcd.kcdsearch.utils.globalMethods.withLoading
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchService @Inject constructor(
    private val searchQueryService: SearchQueryService,
    private val searchRepository: SearchRepository,
    private val appSearchRepository: AppSearchRepository,
    private val contactSearchRepository: ContactSearchRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val quickSearchRepository: QuickSearchRepository
) {
    private val _webResults = MutableStateFlow<List<SearchResult>>(emptyList())
    private val _appResults = MutableStateFlow<List<AppResult>>(emptyList())
    private val _contactResults = MutableStateFlow<List<ContactResult>>(emptyList())

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

    private val _suggestions = MutableStateFlow<List<SuggestionItem>>(emptyList())
    val suggestions = _suggestions.asStateFlow()

    private val _category = MutableStateFlow(SearchCategory.General)
    val category = _category.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches = _recentSearches.asStateFlow()

    private var suggestionsJob: Job? = null

    fun loadRecentSearches(limit: Int, scope: CoroutineScope) {
        scope.launch {
            _recentSearches.update { searchRepository.getRecentSearchQueries(limit) }
        }
    }

    fun requestSuggestionsDebounced(scope: CoroutineScope) {
        clearSuggestions()
        suggestionsJob?.cancel()
        _isAutocompleteLoading.value = true
        suggestionsJob = scope.launch {
            delay(500)
            getAutocompleteSuggestions()
        }
    }

    fun requestSuggestionsImmediate(scope: CoroutineScope) {
        suggestionsJob?.cancel()
        _isAutocompleteLoading.value = false
        scope.launch {
            getAutocompleteSuggestions()
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
                    launch {
                        Log.d("SearchService", "[CONTACT] starting contact search")
                        val contactItems = contactSearchRepository.search(
                            ContactSearchRequestDto(
                                query = q
                            )
                        )
                        Log.d("SearchService", "[CONTACT] completed: ${contactItems.size} results")
                        _contactResults.value = contactItems
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

    suspend fun getAutocompleteSuggestions() {
        val query = searchQueryService.query.value.trim()

        if (query.isEmpty()) {
            _suggestions.value = searchRepository.getRecentSearchQueries(limit = 15)
                .map { SuggestionItem.Text(it) }
            _isAutocompleteLoading.value = false
            return
        }

        if (query.length <= 2) {
            withLoading(_isAutocompleteLoading, _autocompleteErrors) {
                val appMatches = appSearchRepository.search(AppSearchRequestDto(query = query, category = null))
                val searchActions = quickSearchRepository.search(query)
                _suggestions.value = appMatches.take(5).map { SuggestionItem.App(it) } +
                        searchActions.map { SuggestionItem.SearchAction(it) }
            }
            return
        }

        withLoading(_isAutocompleteLoading, _autocompleteErrors) {
            var appItems = emptyList<SuggestionItem.App>()
            var apiItems = emptyList<SuggestionItem.Text>()
            var contactItems = emptyList<SuggestionItem.Contact>()
            val searchActions = quickSearchRepository.search(query)
                .map { SuggestionItem.SearchAction(it) }

            fun publishSuggestions() {
                _suggestions.value = appItems + contactItems + apiItems + searchActions
            }


            coroutineScope {
                launch {
                    val matches = appSearchRepository.search(AppSearchRequestDto(query = query, category = null))
                        .take(5)
                        .map { SuggestionItem.App(it) }
                    appItems = matches
                    publishSuggestions()
                }
                launch {
                    val contactMatches = contactSearchRepository.search(ContactSearchRequestDto(query = query))
                        .take(5)
                        .map { SuggestionItem.Contact(it) }
                    contactItems = contactMatches
                    publishSuggestions()
                }
                launch {
                    val texts = searchRepository.autocomplete(query)
                        .getOrElse { emptyList() }
                        .take(10)
                        .map { SuggestionItem.Text(it) }
                    apiItems = texts
                    publishSuggestions()
                }
            }
        }
    }

    fun clear(scope: CoroutineScope) {
        searchQueryService.clearQuery()
        _category.value = SearchCategory.General
        resetPagination()
        requestSuggestionsDebounced(scope)
    }

    fun clearSuggestions() {
        _suggestions.value = emptyList()
    }

    fun clearAutocompleteError() {
        _autocompleteErrors.value = null
    }

    fun setCategory(category: SearchCategory) {
        _category.value = category
    }

    fun clearError() {
        _error.value = null
    }

    fun contactRequiresPermission(): Boolean = contactSearchRepository.requiresPermission()
    fun isWhatsappInstalled(): Boolean = contactSearchRepository.isWhatsappInstalled()
    fun getContactUriByNumber(number: String) = contactSearchRepository.getContactUriByNumber(number)

    fun deleteRecentSearch(query: String, limit: Int, scope: CoroutineScope) {
        scope.launch {
            searchHistoryRepository.deleteQuery(query)
            _recentSearches.update { searchRepository.getRecentSearchQueries(limit) }
        }
    }

    private fun resetPagination() {
        _currentPage.value = 1
        _webResults.value = emptyList()
        _appResults.value = emptyList()
        _infoboxes.value = emptyList()
        _contactResults.value = emptyList()
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
        _results.value = _appResults.value.map { UnifiedSearchResult.App(it) } +
                _contactResults.value.map { UnifiedSearchResult.Contact(it) } +
                _webResults.value.map { UnifiedSearchResult.Web(it) }
   }

    fun restoreRecentSearch(query: String, limit: Int, scope: CoroutineScope) {
        scope.launch {
            searchHistoryRepository.upsertQuery(query)
            _recentSearches.update { searchRepository.getRecentSearchQueries(limit) }
        }
    }

    private fun canLoadMore(): Boolean =
        searchQueryService.query.value.isNotBlank() && !_isLoading.value && _hasMorePages.value
}