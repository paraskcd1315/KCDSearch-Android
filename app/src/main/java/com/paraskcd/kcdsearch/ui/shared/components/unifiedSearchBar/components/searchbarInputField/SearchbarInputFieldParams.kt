package com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.searchbarInputField

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarState
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
data class SearchbarInputFieldParams(
    val query: String,
    val onQueryChange: (String) -> Unit,
    val searchBarState: SearchBarState,
    val scope: CoroutineScope,
    val placeholder: String,
)
