package com.paraskcd.kcdsearch.model

import com.paraskcd.kcdsearch.data.api.apps.dataSources.AppResult
import com.paraskcd.kcdsearch.data.api.search.dataSources.searchResult.SearchResult

sealed class UnifiedSearchResult {
    data class Web(val item: SearchResult): UnifiedSearchResult()
    data class App(val item: AppResult): UnifiedSearchResult()
}