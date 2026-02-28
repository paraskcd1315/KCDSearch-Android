package com.paraskcd.kcdsearch.ui.modules.search.screens

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.paraskcd.kcdsearch.model.UnifiedSearchResult
import com.paraskcd.kcdsearch.ui.modules.search.SearchViewModel
import com.paraskcd.kcdsearch.ui.modules.search.components.infoboxesAccordion.InfoboxesAccordion
import com.paraskcd.kcdsearch.ui.modules.search.components.searchTabs.SearchTabs
import com.paraskcd.kcdsearch.ui.modules.search.components.searchTabs.SearchTabsParams
import com.paraskcd.kcdsearch.ui.modules.search.components.searchTabs.searchTabs
import com.paraskcd.kcdsearch.ui.shared.components.kcdsearchLogo.KCDSearchLogo
import com.paraskcd.kcdsearch.ui.shared.components.kcdsearchLogo.KCDSearchLogoParams
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.UnifiedSearchBar
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.UnifiedSearchBarParams
import com.paraskcd.kcdsearch.ui.shared.layouts.ScreenColumnLayout
import com.paraskcd.kcdsearch.ui.shared.layouts.ScreenColumnLayoutParams

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    val infoboxes by viewModel.infoboxes.collectAsState()
    val appResults = results.filterIsInstance<UnifiedSearchResult.App>().map { it.item }
    val webResults = results.filterIsInstance<UnifiedSearchResult.Web>().map { it.item }
    val isLoading by viewModel.isLoading.collectAsState()
    val errors by viewModel.errors.collectAsState()
    val areSuggestionsLoading by viewModel.isSuggestionsLoading.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val scope = rememberCoroutineScope()
    val searchBarState = rememberSearchBarState()
    val listState = rememberLazyListState()
    val context = LocalContext.current
    var showLogo by remember { mutableStateOf(true) }
    var lastScrollOffset: Int by remember { mutableStateOf(0) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val selectedCategory = searchTabs[selectedTabIndex].value

    LaunchedEffect(listState) {
        snapshotFlow {
            listState.firstVisibleItemScrollOffset + listState.firstVisibleItemIndex * 1000
        }.collect { offset ->
            val isScrollingDown = offset > lastScrollOffset
            lastScrollOffset = offset
            showLogo = !isScrollingDown || offset < 100
        }
    }

    BackHandler(enabled = searchBarState.currentValue == SearchBarValue.Collapsed) {
        viewModel.clearQuery()
        (context as? ComponentActivity)?.finish()
    }

    ScreenColumnLayout(
        params = ScreenColumnLayoutParams(
            gradientColors = listOf(
                MaterialTheme.colorScheme.surfaceContainerHigh,
                MaterialTheme.colorScheme.surface
            ),
            verticalArrangement = Arrangement.Top
        )
    ) {
        AnimatedVisibility(
            visible = showLogo,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            KCDSearchLogo(
                params = KCDSearchLogoParams(height = 100.dp)
            )
        }
        UnifiedSearchBar(
            params = UnifiedSearchBarParams(
                query = query,
                onQueryChange = viewModel::setQuery,
                searchBarState = searchBarState,
                scope = scope,
                placeholder = "Search...",
                suggestions = suggestions,
                isLoading = areSuggestionsLoading,
                onSuggestionClick = { suggestion ->
                    //TODO
                }
            )
        )
        SearchTabs(
            params = SearchTabsParams(
                selectedCategory = selectedCategory,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it },
                listState = listState,
                contentPaddingBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                modifier = Modifier.weight(1f)
            )
        )
    }
}