package com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.autocompleteSuggestions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier

fun Modifier.autocompleteSuggestionDefault(onClick: () -> Unit): Modifier =
    this.fillMaxWidth()
        .clickable { onClick() }