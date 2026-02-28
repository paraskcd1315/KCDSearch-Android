package com.paraskcd.kcdsearch.ui.modules.search.components.searchTabs

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.paraskcd.kcdsearch.ui.modules.search.enums.SearchCategory

data class SearchTabsParams(
    val selectedCategory: SearchCategory,
    val selectedTabIndex: Int,
    val onTabSelected: (Int) -> Unit,
    val listState: LazyListState,
    val contentPaddingBottom: Dp,
    val modifier: Modifier = Modifier,
    val generalContent: (@Composable () -> Unit)? = null,
    val imagesContent: (@Composable () -> Unit)? = null,
)
