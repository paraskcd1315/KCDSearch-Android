package com.paraskcd.kcdsearch.ui.modules.search.components.searchTabs.components.searchTabsRow

import androidx.compose.ui.Modifier
import com.paraskcd.kcdsearch.ui.modules.search.data.SearchTab

data class SearchTabsRowParams(
    val tabs: List<SearchTab>,
    val selectedIndex: Int,
    val onTabSelected: (Int) -> Unit,
    val modifier: Modifier = Modifier,
)
