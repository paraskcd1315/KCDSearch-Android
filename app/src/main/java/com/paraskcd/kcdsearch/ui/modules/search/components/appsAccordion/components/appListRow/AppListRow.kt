package com.paraskcd.kcdsearch.ui.modules.search.components.appsAccordion.components.appListRow

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paraskcd.kcdsearch.ui.shared.components.listItemRow.ListItemRow
import com.paraskcd.kcdsearch.ui.shared.components.listItemRow.ListItemRowParams

@Composable
fun AppListRow(params: AppListRowParams) {
    ListItemRow(
        params = ListItemRowParams(
            label = params.app.label,
            leadingContent = {
                val icon = params.getAppIcon(params.app.packageName)
                if (icon != null) {
                    Image(
                        bitmap = icon,
                        contentDescription = params.app.label,
                        modifier = Modifier.size(40.dp)
                    )
                }
            },
            onClick = { params.launchApp(params.app.packageName) },
            modifier = params.modifier
        )
    )
}