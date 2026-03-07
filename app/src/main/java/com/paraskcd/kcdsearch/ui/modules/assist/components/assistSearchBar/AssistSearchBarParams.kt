package com.paraskcd.kcdsearch.ui.modules.assist.components.assistSearchBar

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import com.paraskcd.kcdsearch.model.SuggestionItem

data class AssistSearchBarParams(
    val query: String,
    val onQueryChange: (String) -> Unit,
    val onClear: () -> Unit,
    val onSearchSubmit: (String) -> Unit = {},
    val supportsBlur: Boolean,
    val modifier: Modifier = Modifier,
    val placeholder: String = "Search...",
    val isLoading: Boolean = false,
    val suggestions: List<SuggestionItem> = emptyList(),
    val getAppIcon: (String) -> ImageBitmap? = { null },
    val onSuggestionClick: (SuggestionItem) -> Unit = {},
    val autocompleteError: Throwable? = null,
    val onClearAutocompleteError: () -> Unit = {},
)
