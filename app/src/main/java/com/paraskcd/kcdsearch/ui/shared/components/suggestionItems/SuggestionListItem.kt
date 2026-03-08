package com.paraskcd.kcdsearch.ui.shared.components.suggestionItems

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SuggestionListItem(params: SuggestionListItemParams) {
    ListItem(
        headlineContent = { Text(params.headline) },
        supportingContent = params.supporting?.let { { Text(it) } },
        leadingContent = params.leading,
        trailingContent = params.trailing,
        colors = ListItemDefaults.colors(containerColor = params.containerColor),
        modifier = params.modifier
            .fillMaxWidth()
            .clickable { params.onClick() }
    )
}