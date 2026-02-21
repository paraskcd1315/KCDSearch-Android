package com.paraskcd.kcdsearch.services

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class SearchService @Inject constructor() {
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    fun setQuery(query: String) {
        _query.value = query
    }
}