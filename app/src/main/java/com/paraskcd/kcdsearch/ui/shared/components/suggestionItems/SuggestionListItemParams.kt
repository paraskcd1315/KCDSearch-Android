package com.paraskcd.kcdsearch.ui.shared.components.suggestionItems

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

data class SuggestionListItemParams(
    val headline: String,
    val supporting: String? = null,
    val leading: @Composable (() -> Unit)? = null,
    val trailing: @Composable (() -> Unit)? = null,
    val containerColor: Color,
    val onClick: () -> Unit,
    val modifier: Modifier = Modifier
)
