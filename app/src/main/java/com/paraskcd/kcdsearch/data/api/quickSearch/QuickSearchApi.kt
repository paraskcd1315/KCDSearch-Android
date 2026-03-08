package com.paraskcd.kcdsearch.data.api.quickSearch

import com.paraskcd.kcdsearch.data.api.quickSearch.dataSources.QuickSearchResult

interface QuickSearchApi {
    fun getSearchActions(query: String): List<QuickSearchResult>
    fun isInstalled(packageName: String): Boolean
}