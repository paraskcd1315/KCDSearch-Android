package com.paraskcd.kcdsearch.ui.activities.home.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.composables.icons.heroicons.Heroicons
import com.composables.icons.heroicons.outline.Clock
import com.composables.icons.heroicons.outline.Trash
import com.paraskcd.kcdsearch.model.SuggestionItem
import com.paraskcd.kcdsearch.ui.activities.home.HomeViewModel
import com.paraskcd.kcdsearch.ui.activities.home.components.weatherWidget.WeatherWidget
import com.paraskcd.kcdsearch.ui.activities.home.components.weatherWidget.WeatherWidgetParams
import com.paraskcd.kcdsearch.ui.shared.components.kcdsearchLogo.KCDSearchLogo
import com.paraskcd.kcdsearch.ui.shared.components.kcdsearchLogo.KCDSearchLogoParams
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.UnifiedSearchBar
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.UnifiedSearchBarParams
import com.paraskcd.kcdsearch.ui.shared.layouts.ScreenColumnLayout
import com.paraskcd.kcdsearch.ui.shared.layouts.ScreenColumnLayoutParams
import com.paraskcd.kcdsearch.utils.extensionMethods.segmentedListItems
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val query by viewModel.query.collectAsState()
    val areSuggestionsLoading by viewModel.isLoading.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val autocompleteError by viewModel.autocompleteErrors.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val useFahrenheit by viewModel.useFahrenheit.collectAsState()
    val weatherIsLoading by viewModel.weatherIsLoading.collectAsState()
    val weatherError by viewModel.weatherError.collectAsState()
    val weatherForecast by viewModel.weatherForecast.collectAsState()
    val weatherRequiresPermission by viewModel.weatherRequiresPermission.collectAsState()
    val weatherCityName by viewModel.weatherCityName.collectAsState()
    var pendingDelete by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val searchBarState = rememberSearchBarState()

    val pullToRefreshState = rememberPullToRefreshState()
    val isRefreshing by viewModel.weatherIsLoading.collectAsState()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onLocationPermissionResult(granted)
    }

    LaunchedEffect(Unit) {
        viewModel.loadRecentSearches()
    }

    LaunchedEffect(pendingDelete) {
        val query = pendingDelete ?: return@LaunchedEffect
        viewModel.deleteRecentSearch(query)
        val result = snackbarHostState.showSnackbar(
            message = "\"$query\" removed",
            actionLabel = "Undo",
            duration = SnackbarDuration.Short
        )
        if (result == SnackbarResult.ActionPerformed) {
            viewModel.restoreRecentSearch(query)
        }
        pendingDelete = null
    }


    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadRecentSearches()
                scope.launch {
                    viewModel.loadWeather()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            scope.launch { viewModel.loadWeather() }
            viewModel.loadRecentSearches()
        },
        state = pullToRefreshState
    ) {
        ScreenColumnLayout(
            params = ScreenColumnLayoutParams(
                gradientColors = listOf(
                    MaterialTheme.colorScheme.surfaceContainerHigh,
                    MaterialTheme.colorScheme.surface
                ),
                snackbarHostState = snackbarHostState
            )
        ) {
            KCDSearchLogo(
                params = KCDSearchLogoParams(
                    height = 150.dp
                )
            )
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
                    onQuerySubmit = { query ->
                        if (query.isNotBlank()) {
                            viewModel.onSuggestionClick(SuggestionItem.Text(query))
                        }
                    },
                    onSuggestionClick = viewModel::onSuggestionClick,
                    autocompleteError = autocompleteError,
                    onClearAutocompleteError = viewModel::clearAutocompleteError,
                    onSearchbarCollapse = viewModel::onSearchbarCollapse
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            WeatherWidget(
                params = WeatherWidgetParams(
                    isLoading = weatherIsLoading,
                    error = weatherError,
                    forecast = weatherForecast,
                    cityName = weatherCityName,
                    requiresPermission = weatherRequiresPermission,
                    onRequestPermission = {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                    },
                    onRetry = { scope.launch { viewModel.loadWeather() } },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    useFahrenheit = useFahrenheit
                )
            )

            if (recentSearches.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                LazyColumn(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                    item {
                        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                            Text(
                                text = "Recent searches",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    segmentedListItems(
                        items = recentSearches,
                        key = { index, queryText -> "RecentSearch_${index}_$queryText" }
                    ) { _, queryText, _, _ ->
                        val dismissState = rememberSwipeToDismissBoxState()

                        LaunchedEffect(dismissState.currentValue) {
                            if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                                pendingDelete = queryText
                            }
                        }

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.errorContainer),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Heroicons.Outline.Trash,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.padding(end = 16.dp)
                                    )
                                }
                            },
                            enableDismissFromStartToEnd = false
                        ) {
                            ListItem(
                                headlineContent = {
                                    Text(
                                        text = queryText,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                leadingContent = {
                                    Icon(
                                        imageVector = Heroicons.Outline.Clock,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.onSuggestionClick(SuggestionItem.Text(queryText))
                                    },
                                colors = ListItemDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}