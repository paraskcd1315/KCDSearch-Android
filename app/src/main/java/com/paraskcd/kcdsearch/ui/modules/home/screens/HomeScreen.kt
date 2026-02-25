package com.paraskcd.kcdsearch.ui.modules.home.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.paraskcd.kcdsearch.ui.modules.home.HomeViewModel
import com.paraskcd.kcdsearch.ui.shared.components.kcdsearchLogo.KCDSearchLogo
import com.paraskcd.kcdsearch.ui.shared.components.kcdsearchLogo.KCDSearchLogoParams
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.UnifiedSearchBar
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.UnifiedSearchBarParams

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {
    val query by viewModel.query.collectAsState()
    val areSuggestionsLoading by viewModel.isLoading.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val scope = rememberCoroutineScope()
    val searchBarState = rememberSearchBarState()

    Scaffold(
        modifier = Modifier
            .scaffoldModifiers(),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .backgroundGradientModifiers(
                    listOf(
                        MaterialTheme.colorScheme.surfaceContainerHigh,
                        MaterialTheme.colorScheme.surface
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
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
                        onSuggestionClick = { suggestion ->
                            viewModel.setQuery(suggestion)
                        }
                    )
                )
            }
        }
    }
}