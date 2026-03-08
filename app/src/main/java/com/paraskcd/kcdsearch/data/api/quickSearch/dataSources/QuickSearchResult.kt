package com.paraskcd.kcdsearch.data.api.quickSearch.dataSources

data class QuickSearchResult(
    val query: String,
    val title: String,
    val subtitle: String,
    val packageName: String
)