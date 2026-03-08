package com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.components.appItem

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
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.AssistResultsListParams

@Composable
fun AppItem(params: AppItemParams, parentParams: AssistResultsListParams) {
    val icon = parentParams.getAppIcon(params.app.item.packageName)

    ListItem(
        headlineContent = { Text(params.app.item.label) },
        supportingContent = { Text(params.app.item.packageName) },
        leadingContent = {
            if (icon != null) {
                Image(
                    bitmap = icon,
                    contentDescription = params.app.item.label,
                    modifier = Modifier.size(40.dp)
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { parentParams.onSuggestionClick(params.app) }
    )

}