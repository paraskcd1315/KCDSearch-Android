package com.paraskcd.kcdsearch.ui.shared.components.listItemRow

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class ListItemRowParams(
    val label: String,
    val leadingContent: @Composable () -> Unit,
    val modifier: Modifier = Modifier,
    val onClick: () -> Unit = {},
)
