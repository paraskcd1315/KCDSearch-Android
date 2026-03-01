package com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import com.paraskcd.kcdsearch.model.SuggestionItem
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
    val suggestions: List<SuggestionItem> = emptyList(),
    val getAppIcon: (String) -> ImageBitmap? = { null },
    val onQuerySubmit: (String) -> Unit = {},
    val onSuggestionClick: (SuggestionItem) -> Unit = {},
    val onSearchBarExpanded: () -> Unit = {},
    val autocompleteError: Throwable? = null,
    val onClearAutocompleteError: () -> Unit = {},
)