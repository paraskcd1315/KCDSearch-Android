package com.paraskcd.kcdsearch.data.dtos

import com.paraskcd.kcdsearch.data.api.apps.enums.AppCategory

data class AppSearchRequestDto(
    val query: String,
    val category: AppCategory? = null
)
