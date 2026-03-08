package com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.components.textItem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.composables.icons.heroicons.Heroicons
import com.composables.icons.heroicons.outline.MagnifyingGlass
import com.composables.icons.heroicons.outline.User
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.AssistResultsListParams
import com.paraskcd.kcdsearch.ui.shared.components.iconCircle.IconCircle
import com.paraskcd.kcdsearch.ui.shared.components.iconCircle.IconCircleParams

@Composable
fun TextItem(params: TextItemParams, parentParams: AssistResultsListParams) {
    ListItem(
        headlineContent = { Text(params.text.value) },
        leadingContent = {
            IconCircle(
                IconCircleParams(Heroicons.Outline.MagnifyingGlass)
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { parentParams.onSuggestionClick(params.text) }
    )
}