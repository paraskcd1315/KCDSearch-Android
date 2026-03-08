package com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.components.searchActionItem

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composables.icons.heroicons.Heroicons
import com.composables.icons.heroicons.outline.ArrowTopRightOnSquare
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.AssistResultsListParams
import com.paraskcd.kcdsearch.ui.shared.components.iconCircle.IconCircle
import com.paraskcd.kcdsearch.ui.shared.components.iconCircle.IconCircleParams

@Composable
fun SearchActionItem(params: SearchActionItemParams, parentParams: AssistResultsListParams) {
    val icon = parentParams.getAppIcon(params.searchAction.item.packageName)

    ListItem(
        headlineContent = { Text(params.searchAction.item.title) },
        supportingContent = { Text(params.searchAction.item.subtitle) },
        leadingContent = {
            if (icon != null) {
                Image(
                    bitmap = icon,
                    contentDescription = params.searchAction.item.subtitle,
                    modifier = Modifier.size(40.dp)
                )
            } else {
                IconCircle(
                    IconCircleParams(Heroicons.Outline.ArrowTopRightOnSquare)
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { parentParams.onSuggestionClick(params.searchAction) }
    )
}