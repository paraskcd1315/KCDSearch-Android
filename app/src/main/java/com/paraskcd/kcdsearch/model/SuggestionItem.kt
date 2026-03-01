package com.paraskcd.kcdsearch.model

import com.paraskcd.kcdsearch.data.api.apps.dataSources.AppResult

sealed class SuggestionItem {
    data class Text(val value: String): SuggestionItem()
    data class App(val item: AppResult): SuggestionItem()
}