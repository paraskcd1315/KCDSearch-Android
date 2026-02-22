package com.paraskcd.kcdsearch.ui.shared.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    searchBarState: SearchBarState,
    scope: CoroutineScope,
    modifier: Modifier = Modifier,
    placeholder: String = "Search...",
    expandedContent: @Composable (collapse: () -> Unit) -> Unit,
) {
    SearchBar(
        state = searchBarState,
        inputField = {
            SearchBarInputField(
                query = query,
                onQueryChange = onQueryChange,
                searchBarState = searchBarState,
                scope = scope,
                placeholder = placeholder,
            )
        },
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
    )

    ExpandedFullScreenSearchBar(
        state = searchBarState,
        inputField = {
            SearchBarInputField(
                query = query,
                onQueryChange = onQueryChange,
                searchBarState = searchBarState,
                scope = scope,
                placeholder = placeholder,
            )
        },
    ) {
        expandedContent({ scope.launch { searchBarState.animateToCollapsed() } })
    }
}