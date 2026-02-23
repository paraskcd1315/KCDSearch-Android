package com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarState
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
data class UnifiedSearchBarParams(
    val query: String,
    val onQueryChange: (String) -> Unit,
    val searchBarState: SearchBarState,
    val scope: CoroutineScope,
    val modifier: Modifier = Modifier,
    val placeholder: String = "Search...",
    val isLoading: Boolean = false,
    val suggestions: List<String> = emptyList(),
    val onSuggestionClick: (String) -> Unit = {},
)