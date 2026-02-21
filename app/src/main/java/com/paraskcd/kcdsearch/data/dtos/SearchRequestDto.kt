package com.paraskcd.kcdsearch.data.dtos

data class SearchRequestDto(
    val query: String,
    val pageno: Int = 1,
    val language: String? = null,
    val categories: String? = null,
    val engines: String? = null,
    val safesearch: Int? = null,
)
