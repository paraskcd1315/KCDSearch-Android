package com.paraskcd.kcdsearch.ui.modules.search.screens

import android.content.Intent
import android.provider.Settings
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.composables.icons.heroicons.Heroicons
import com.composables.icons.heroicons.outline.ExclamationTriangle
import com.paraskcd.kcdsearch.constants.GlobalConstants.IMAGE_SKELETON_ASPECT_RATIOS
import com.paraskcd.kcdsearch.data.api.search.dataSources.searchResult.SearchResult
import com.paraskcd.kcdsearch.model.UnifiedSearchResult
import com.paraskcd.kcdsearch.ui.modules.search.SearchViewModel
import com.paraskcd.kcdsearch.ui.modules.search.components.appsAccordion.AppsAccordion
import com.paraskcd.kcdsearch.ui.modules.search.components.appsAccordion.AppsAccordionParams
import com.paraskcd.kcdsearch.ui.modules.search.components.appsAccordion.components.appListRow.AppListRow
import com.paraskcd.kcdsearch.ui.modules.search.components.appsAccordion.components.appListRow.AppListRowParams
import com.paraskcd.kcdsearch.ui.modules.search.components.contactsAccordion.ContactsAccordion
import com.paraskcd.kcdsearch.ui.modules.search.components.contactsAccordion.ContactsAccordionParams
import com.paraskcd.kcdsearch.ui.modules.search.components.contactsAccordion.components.contactListRow.ContactListRow
import com.paraskcd.kcdsearch.ui.modules.search.components.contactsAccordion.components.contactListRow.ContactListRowParams
import com.paraskcd.kcdsearch.ui.modules.search.components.fullscreenImageModal.FullscreenImageModal
import com.paraskcd.kcdsearch.ui.modules.search.components.fullscreenImageModal.FullscreenImageModalParams
import com.paraskcd.kcdsearch.ui.modules.search.components.imageResultSkeleton.ImageResultSkeletonParams
import com.paraskcd.kcdsearch.ui.modules.search.components.infoboxAccordion.InfoboxAccordion
import com.paraskcd.kcdsearch.ui.modules.search.components.infoboxAccordion.InfoboxAccordionParams
import com.paraskcd.kcdsearch.ui.modules.search.components.searchImageResult.ImageResultSkeleton
import com.paraskcd.kcdsearch.ui.modules.search.components.searchImageResult.SearchImageResult
import com.paraskcd.kcdsearch.ui.modules.search.components.searchImageResult.SearchImageResultParams
import com.paraskcd.kcdsearch.ui.modules.search.components.searchResultSkeleton.SearchResultSkeleton
import com.paraskcd.kcdsearch.ui.modules.search.components.searchResultSkeleton.SearchResultSkeletonParams
import com.paraskcd.kcdsearch.ui.modules.search.components.searchTabs.SearchTabs
import com.paraskcd.kcdsearch.ui.modules.search.components.searchTabs.SearchTabsParams
import com.paraskcd.kcdsearch.ui.modules.search.components.searchTabs.contents.generalContent.GeneralContentParams
import com.paraskcd.kcdsearch.ui.modules.search.components.searchTabs.contents.generalContent.generalContent
import com.paraskcd.kcdsearch.ui.modules.search.components.searchTabs.contents.imageContent.ImagesContentParams
import com.paraskcd.kcdsearch.ui.modules.search.components.searchTabs.contents.imageContent.imagesContent
import com.paraskcd.kcdsearch.ui.modules.search.components.searchTabs.searchTabs
import com.paraskcd.kcdsearch.ui.modules.search.components.webResultCard.WebResultCard
import com.paraskcd.kcdsearch.ui.modules.search.components.webResultCard.WebResultCardParams
import com.paraskcd.kcdsearch.ui.modules.search.enums.SearchCategory
import com.paraskcd.kcdsearch.ui.shared.components.kcdsearchLogo.KCDSearchLogo
import com.paraskcd.kcdsearch.ui.shared.components.kcdsearchLogo.KCDSearchLogoParams
import com.paraskcd.kcdsearch.ui.shared.components.listItemRow.ListItemRow
import com.paraskcd.kcdsearch.ui.shared.components.listItemRow.ListItemRowParams
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.UnifiedSearchBar
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.UnifiedSearchBarParams
import com.paraskcd.kcdsearch.ui.shared.layouts.ScreenColumnLayout
import com.paraskcd.kcdsearch.ui.shared.layouts.ScreenColumnLayoutParams
import com.paraskcd.kcdsearch.utils.extensionMethods.getImageUrl

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
    val contactResults = results.filterIsInstance<UnifiedSearchResult.Contact>().map { it.item }
    val isLoading by viewModel.isLoading.collectAsState()
    val errors by viewModel.errors.collectAsState()
    val autocompleteErrors by viewModel.autocompleteErrors.collectAsState()
    val areSuggestionsLoading by viewModel.isSuggestionsLoading.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val hasMorePages by viewModel.hasMorePages.collectAsState()
    val scope = rememberCoroutineScope()
    val searchBarState = rememberSearchBarState()
    val listState = rememberLazyListState()
    val staggeredGridState = rememberLazyStaggeredGridState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var failedImageUrls by remember { mutableStateOf(setOf<String>()) }
    var selectedImageForModal by remember { mutableStateOf<SearchResult?>(null) }
    val selectedTabIndex = remember(category) {
        searchTabs.indexOfFirst { it.value == category }.takeIf { it >= 0 } ?: 0
    }
    val selectedCategory = searchTabs[selectedTabIndex].value
    val showLogo by remember(listState, staggeredGridState, selectedCategory) {
        derivedStateOf {
            val (firstIndex, scrollOffset) = when (selectedCategory) {
                SearchCategory.Images -> {
                    staggeredGridState.firstVisibleItemIndex to
                            staggeredGridState.firstVisibleItemScrollOffset
                }
                else -> {
                    listState.firstVisibleItemIndex to
                            listState.firstVisibleItemScrollOffset
                }
            }
            firstIndex == 0 && scrollOffset < 150
        }
    }


    val displayableImages = remember(webResults, failedImageUrls) {
        webResults.filter { result ->
            result.getImageUrl()?.let { url -> url !in failedImageUrls } ?: false
        }
    }

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

    LaunchedEffect(staggeredGridState) {
        snapshotFlow {
            val layoutInfo = staggeredGridState.layoutInfo
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

    LaunchedEffect(query) {
        failedImageUrls = emptySet()
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
                    getAppIcon = { viewModel.getAppIcon(it) },
                    onQuerySubmit = { viewModel.submitSearch(it) },
                    onSuggestionClick = viewModel::onSuggestionClick,
                    autocompleteError = autocompleteErrors,
                    onClearAutocompleteError = viewModel::clearAutocompleteError
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
                    staggeredGridState = staggeredGridState,
                    contentPaddingBottom = WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding(),
                    modifier = Modifier.weight(1f),
                    generalContent = {
                        generalContent(
                            GeneralContentParams(
                                isLoading = isLoading,
                                query = query,
                                webResults = webResults,
                                infoboxes = infoboxes,
                                appResults = appResults,
                                contactResults = contactResults,
                                hasMorePages = hasMorePages,
                                viewModel = viewModel,
                                context = context,
                            )
                        )
                    },
                    imagesContent = {
                        imagesContent(
                            ImagesContentParams(
                                isLoading = isLoading,
                                query = query,
                                displayableImages = displayableImages,
                                hasMorePages = hasMorePages,
                                onImageSelected = { selectedImageForModal = it },
                                onImageLoadFailed = { url ->
                                    failedImageUrls = failedImageUrls + url
                                },
                            )
                        )
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
        selectedImageForModal?.let { result ->
            result.getImageUrl()?.let { imageUrl ->
                FullscreenImageModal(
                    params = FullscreenImageModalParams(
                        imageUrl = imageUrl,
                        pageUrl = result.url,
                        title = result.title,
                        onDismiss = { selectedImageForModal = null },
                        onOpenUrl = { viewModel.openUrl(it) },
                        onShare = { viewModel.shareImage(it) },
                        onDownload = { viewModel.downloadImage(it) }
                    )
                )
            }
        }
    }
}