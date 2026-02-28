package com.paraskcd.kcdsearch.ui.modules.search.components.searchTabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.paraskcd.kcdsearch.ui.modules.search.components.searchTabs.components.searchTabsRow.SearchTabsRow
import com.paraskcd.kcdsearch.ui.modules.search.components.searchTabs.components.searchTabsRow.SearchTabsRowParams
import com.paraskcd.kcdsearch.ui.modules.search.enums.SearchCategory

@Composable
fun SearchTabs(params: SearchTabsParams) {
    Column(
        modifier = params.modifier
            .fillMaxSize()
            .padding(top = 24.dp)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        SearchTabsRow(
            params = SearchTabsRowParams(
                tabs = searchTabs,
                selectedIndex = params.selectedTabIndex,
                onTabSelected = params.onTabSelected,
                modifier = Modifier
            )
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            thickness = 1.dp
        )
        LazyColumn(
            state = params.listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
        ) {
            when (params.selectedCategory) {
                SearchCategory.Images -> params.imagesContent?.invoke(this)
                else -> params.generalContent?.invoke(this)
            }
        }
    }
}