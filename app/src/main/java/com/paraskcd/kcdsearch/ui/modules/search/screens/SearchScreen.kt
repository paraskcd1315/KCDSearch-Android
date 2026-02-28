package com.paraskcd.kcdsearch.ui.modules.search.screens

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.paraskcd.kcdsearch.model.UnifiedSearchResult
import com.paraskcd.kcdsearch.ui.modules.search.SearchViewModel
import com.paraskcd.kcdsearch.ui.modules.search.components.appsAccordion.AppsAccordion
import com.paraskcd.kcdsearch.ui.modules.search.components.appsAccordion.AppsAccordionParams
import com.paraskcd.kcdsearch.ui.modules.search.components.appsAccordion.components.appListRow.AppListRow
import com.paraskcd.kcdsearch.ui.modules.search.components.appsAccordion.components.appListRow.AppListRowParams
import com.paraskcd.kcdsearch.ui.modules.search.components.infoboxAccordion.InfoboxAccordion
import com.paraskcd.kcdsearch.ui.modules.search.components.infoboxAccordion.InfoboxAccordionParams
import com.paraskcd.kcdsearch.ui.modules.search.components.searchResultSkeleton.SearchResultSkeleton
import com.paraskcd.kcdsearch.ui.modules.search.components.searchResultSkeleton.SearchResultSkeletonParams
import com.paraskcd.kcdsearch.ui.modules.search.components.searchTabs.SearchTabs
import com.paraskcd.kcdsearch.ui.modules.search.components.searchTabs.SearchTabsParams
import com.paraskcd.kcdsearch.ui.modules.search.components.searchTabs.searchTabs
import com.paraskcd.kcdsearch.ui.modules.search.components.webResultCard.WebResultCard
import com.paraskcd.kcdsearch.ui.modules.search.components.webResultCard.WebResultCardParams
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
    val category by viewModel.category.collectAsState()
    val results by viewModel.results.collectAsState()
    val infoboxes by viewModel.infoboxes.collectAsState()
    val appResults = results.filterIsInstance<UnifiedSearchResult.App>().map { it.item }
    val webResults = results.filterIsInstance<UnifiedSearchResult.Web>().map { it.item }
    val isLoading by viewModel.isLoading.collectAsState()
    val errors by viewModel.errors.collectAsState()
    val areSuggestionsLoading by viewModel.isSuggestionsLoading.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val hasMorePages by viewModel.hasMorePages.collectAsState()
    val scope = rememberCoroutineScope()
    val searchBarState = rememberSearchBarState()
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val showLogo by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset < 150
        }
    }
    val selectedTabIndex = remember(category) {
        searchTabs.indexOfFirst { it.value == category }.takeIf { it >= 0 } ?: 0
    }
    val selectedCategory = searchTabs[selectedTabIndex].value

    BackHandler(enabled = searchBarState.currentValue == SearchBarValue.Collapsed) {
        viewModel.clearQuery()
        (context as? ComponentActivity)?.finish()
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleIndex to totalItems
        }.collect { (lastVisible, total) ->
            if (total > 0 && lastVisible >= total - 3 && hasMorePages && !isLoading) {
                viewModel.loadNextPage()
            }
        }
    }

    LaunchedEffect(errors) {
        errors?.let { error ->
            snackbarHostState.showSnackbar(
                message = error.message ?: "Something went wrong",
                duration = SnackbarDuration.Short,
                withDismissAction = true
            )
            viewModel.clearError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                    onSuggestionClick = { viewModel.submitSearch(it) },
                )
            )
            SearchTabs(
                params = SearchTabsParams(
                    selectedCategory = selectedCategory,
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { index ->
                        viewModel.setCategoryAndSearch(searchTabs[index].value)
                    },
                    listState = listState,
                    contentPaddingBottom = WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding(),
                    modifier = Modifier.weight(1f),
                    generalContent = {
                        if (isLoading && query.isNotBlank() && webResults.isEmpty() && infoboxes.isEmpty()) {
                            items(5, key = { "skeleton_$it" }) {
                                SearchResultSkeleton(
                                    params = SearchResultSkeletonParams(
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                )
                            }
                        } else {
                            if (appResults.size > 4) {
                                item(key = "apps_accordion") {
                                    AppsAccordion(
                                        params = AppsAccordionParams(
                                            appResults,
                                            getAppIcon = { viewModel.getAppIcon(it) },
                                            launchApp = { viewModel.launchApp(it) }
                                        )
                                    )
                                }
                            } else {
                                items(appResults, key = { it.packageName }) { app ->
                                    AppListRow(
                                        params = AppListRowParams(
                                            app,
                                            modifier = Modifier.padding(16.dp),
                                            getAppIcon = { viewModel.getAppIcon(it) },
                                            launchApp = { viewModel.launchApp(it) }
                                        )
                                    )
                                }
                            }
                            items(
                                infoboxes.filter { it.attributes.isNotEmpty() || it.content.isNotBlank() || it.imgSrc.isNotBlank() },
                                key = { "${it.title}-${it.infobox}-${it.engine}" }
                            ) { infobox ->
                                InfoboxAccordion(
                                    params = InfoboxAccordionParams(
                                        infobox = infobox
                                    )
                                )
                            }
                            items(
                                webResults,
                                key = { it.url }
                            ) { result ->
                                WebResultCard(
                                    params = WebResultCardParams(
                                        result = result,
                                        modifier = Modifier.padding(bottom = 16.dp),
                                        onUrlClick = viewModel::openUrl
                                    )
                                )
                            }
                            if (isLoading && webResults.isNotEmpty()) {
                                item(key = "loading_more") {
                                    SearchResultSkeleton(
                                        params = SearchResultSkeletonParams(
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    )
                                }
                            }
                            if (!hasMorePages && webResults.isNotEmpty()) {
                                item(key = "no_more_results") {
                                    Text(
                                        text = "No more results",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                            alpha = 0.8f
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                )
            )
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}