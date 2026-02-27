package com.paraskcd.kcdsearch.ui.modules.search.screens

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.paraskcd.kcdsearch.ui.modules.search.SearchViewModel
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
    val isLoading by viewModel.isLoading.collectAsState()
    val errors by viewModel.errors.collectAsState()
    val areSuggestionsLoading by viewModel.isSuggestionsLoading.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val scope = rememberCoroutineScope()
    val searchBarState = rememberSearchBarState()
    val context = LocalContext.current

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
        KCDSearchLogo(
            params = KCDSearchLogoParams(
                height = 120.dp
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
                onSuggestionClick = { suggestion ->
                    //TODO
                }
            )
        )
        LazyColumn(
            contentPadding = PaddingValues(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()),
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(top = 24.dp)
                .background(color = MaterialTheme.colorScheme.surfaceContainerHigh, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        ) {

        }
    }
}