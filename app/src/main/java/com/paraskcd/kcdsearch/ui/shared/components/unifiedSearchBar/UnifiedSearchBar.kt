package com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.autocompleteSuggestions.AutocompleteSuggestionParams
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.autocompleteSuggestions.AutocompleteSuggestions
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.searchbarInputField.SearchBarInputField
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.searchbarInputField.SearchbarInputFieldParams

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedSearchBar(params: UnifiedSearchBarParams) {
    val searchbarInputParams = SearchbarInputFieldParams(
        query = params.query,
        onQueryChange = params.onQueryChange,
        searchBarState = params.searchBarState,
        scope = params.scope,
        placeholder = params.placeholder
    )

    SearchBar(
        state = params.searchBarState,
        inputField = {
            SearchBarInputField(
                params = searchbarInputParams
            )
        },
        modifier = params.modifier.fillMaxWidth().padding(horizontal = 24.dp),
    )

    ExpandedFullScreenSearchBar(
        state = params.searchBarState,
        inputField = {
            SearchBarInputField(
                params = searchbarInputParams
            )
        },
    ) {
        AutocompleteSuggestions(
            params = AutocompleteSuggestionParams(
                suggestions = params.suggestions,
                onSuggestionClick = params.onSuggestionClick
            )
        )
    }
}