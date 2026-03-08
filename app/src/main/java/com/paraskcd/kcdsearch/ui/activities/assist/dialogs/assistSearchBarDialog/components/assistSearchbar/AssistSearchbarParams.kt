package com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistSearchBarDialog.components.assistSearchbar

import androidx.compose.ui.Modifier

data class AssistSearchbarParams(
    val query: String,
    val onQueryChange: (String) -> Unit,
    val onClear: () -> Unit,
    val onSearchSubmit: (String) -> Unit,
    val supportsBlur: Boolean,
    val modifier: Modifier = Modifier
)
