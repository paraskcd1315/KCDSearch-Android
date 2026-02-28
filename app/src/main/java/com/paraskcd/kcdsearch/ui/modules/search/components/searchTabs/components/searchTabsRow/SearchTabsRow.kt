package com.paraskcd.kcdsearch.ui.modules.search.components.searchTabs.components.searchTabsRow

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchTabsRow(params: SearchTabsRowParams) {
    PrimaryScrollableTabRow(
        selectedTabIndex = params.selectedIndex,
        modifier = params.modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        divider = {}
    ) {
        params.tabs.forEachIndexed { index, tab ->
            Tab(
                selected = params.selectedIndex == index,
                onClick = { params.onTabSelected(index) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}