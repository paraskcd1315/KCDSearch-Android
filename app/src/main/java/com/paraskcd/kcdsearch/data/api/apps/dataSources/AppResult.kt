package com.paraskcd.kcdsearch.data.api.apps.dataSources

import com.paraskcd.kcdsearch.data.api.apps.enums.AppCategory

data class AppResult(
    val packageName: String,
    val label: String,
    val category: AppCategory? = null,
)