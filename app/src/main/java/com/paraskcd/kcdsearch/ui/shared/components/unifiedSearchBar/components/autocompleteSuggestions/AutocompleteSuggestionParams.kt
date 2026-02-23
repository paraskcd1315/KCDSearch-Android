package com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.autocompleteSuggestions

data class AutocompleteSuggestionParams(
    val suggestions: List<String>,
    val onSuggestionClick: (String) -> Unit,
)
