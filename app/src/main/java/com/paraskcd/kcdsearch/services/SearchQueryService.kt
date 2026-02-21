package com.paraskcd.kcdsearch.services

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SearchQueryService @Inject constructor() {
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    fun setQuery(query: String) {
        _query.value = query
    }

    fun clearQuery() {
        _query.value = ""
    }
}