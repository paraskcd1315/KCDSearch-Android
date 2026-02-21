package com.paraskcd.kcdsearch.data.repositories

import com.paraskcd.kcdsearch.data.api.autocomplete.AutocompleteApi
import com.paraskcd.kcdsearch.data.api.search.SearchApi
import com.paraskcd.kcdsearch.data.api.search.dataSources.searchResult.SearchResultResponse
import com.paraskcd.kcdsearch.data.dtos.SearchRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val searchApi: SearchApi,
    private val autocompleteApi: AutocompleteApi,
) {
    suspend fun search(request: SearchRequestDto): Result<SearchResultResponse> = withContext(
        Dispatchers.IO) {
        runCatching {
            searchApi.search(
                query = request.query,
                pageno = request.pageno,
                language = request.language,
                categories = request.categories,
                engines = request.engines,
                safesearch = request.safesearch,
            )
        }
    }

    suspend fun autocomplete(query: String): Result<List<String>> = withContext(Dispatchers.IO) {
        runCatching {
            val raw = autocompleteApi.autocomplete(query)
            when {
                raw.size == 2 && raw[1] is List<*> ->
                    (raw[1] as List<*>).filterIsInstance<String>()
                else ->
                    raw.filterIsInstance<String>()
            }
        }
    }
}