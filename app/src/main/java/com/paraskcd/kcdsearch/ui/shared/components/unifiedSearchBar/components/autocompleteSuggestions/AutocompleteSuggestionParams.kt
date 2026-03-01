package com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.autocompleteSuggestions

import androidx.compose.ui.graphics.ImageBitmap
import com.paraskcd.kcdsearch.model.SuggestionItem

data class AutocompleteSuggestionParams(
    val suggestions: List<SuggestionItem>,
    val getAppIcon: (String) -> ImageBitmap?,
    val onSuggestionClick: (SuggestionItem) -> Unit,
    val isHistory: Boolean,
    val isLoading: Boolean
)
