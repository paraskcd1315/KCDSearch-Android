package com.paraskcd.kcdsearch.model

import com.paraskcd.kcdsearch.data.api.apps.dataSources.AppResult
import com.paraskcd.kcdsearch.data.api.contacts.dataSources.ContactResult

sealed class SuggestionItem {
    data class Text(val value: String, val isHistory: Boolean = false): SuggestionItem()
    data class App(val item: AppResult): SuggestionItem()
    data class Contact(val item: ContactResult): SuggestionItem()
}