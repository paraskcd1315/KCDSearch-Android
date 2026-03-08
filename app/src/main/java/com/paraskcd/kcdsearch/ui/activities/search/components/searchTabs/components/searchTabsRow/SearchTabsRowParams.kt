package com.paraskcd.kcdsearch.ui.activities.search.components.searchTabs.components.searchTabsRow

import androidx.compose.ui.Modifier
import com.paraskcd.kcdsearch.ui.activities.search.data.SearchTab

data class SearchTabsRowParams(
    val tabs: List<SearchTab>,
    val selectedIndex: Int,
    val onTabSelected: (Int) -> Unit,
    val modifier: Modifier = Modifier,
)
