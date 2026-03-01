package com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar

import android.view.View
import android.view.ViewParent
import android.view.Window
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.autocompleteSuggestions.AutocompleteSuggestionParams
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.autocompleteSuggestions.AutocompleteSuggestions
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.searchbarInputField.SearchBarInputField
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.searchbarInputField.SearchbarInputFieldParams
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.suggestionSkeletonItem.SuggestionSkeletonItem
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.suggestionSkeletonItem.SuggestionSkeletonItemParams
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedSearchBar(params: UnifiedSearchBarParams) {
    val textFieldState = rememberTextFieldState(initialText = params.query)
    val autocompleteSnackbarState = remember { SnackbarHostState() }

    LaunchedEffect(params.query) {
        if (textFieldState.text.toString() != params.query) {
            textFieldState.edit { replace(0, length, params.query) }
        }
    }

    LaunchedEffect(params.autocompleteError) {
        params.autocompleteError?.let { error ->
            autocompleteSnackbarState.showSnackbar(
                message = error.message ?: "Something went wrong",
                duration = SnackbarDuration.Short,
                withDismissAction = true
            )
            params.onClearAutocompleteError()
        }
    }

    val searchbarInputParams = SearchbarInputFieldParams(
        textFieldState = textFieldState,
        query = params.query,
        onQueryChange = params.onQueryChange,
        searchBarState = params.searchBarState,
        scope = params.scope,
        placeholder = params.placeholder,
        onSearchSubmit = params.onQuerySubmit,
    )

    LaunchedEffect(params.searchBarState.currentValue) {
        if (params.searchBarState.currentValue == SearchBarValue.Expanded) {
            params.onSearchBarExpanded()
        }
    }

    SearchBar(
        state = params.searchBarState,
        inputField = {
            SearchBarInputField(
                params = searchbarInputParams
            )
        },
        modifier = params.modifier.fillMaxWidth().padding(horizontal = 24.dp),
    )

    ExpandedFullScreenSearchBar(
        state = params.searchBarState,
        inputField = {
            SearchBarInputField(
                params = searchbarInputParams
            )
        },
        properties = DialogProperties(decorFitsSystemWindows = false),
    ) {
        val view = LocalView.current
        val window = findDialogWindow(view)
        if (window != null) {
            SideEffect {
                window.statusBarColor = android.graphics.Color.TRANSPARENT
                window.decorView.post {
                    WindowCompat.getInsetsController(window, window.decorView).apply {
                        isAppearanceLightStatusBars = false
                        isAppearanceLightNavigationBars = false
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            AutocompleteSuggestions(
                params = AutocompleteSuggestionParams(
                    suggestions = params.suggestions,
                    getAppIcon = params.getAppIcon,
                    onSuggestionClick = { suggestion ->
                        params.scope.launch {
                            params.searchBarState.animateToCollapsed()
                            params.onSuggestionClick(suggestion)
                        }
                    },
                    isHistory = params.query.isBlank(),
                    isLoading = params.isLoading
                )
            )
            SnackbarHost(
                hostState = autocompleteSnackbarState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

private fun findDialogWindow(view: View): Window? {
    var parent: ViewParent? = view.parent
    while (parent != null) {
        if (parent is DialogWindowProvider) {
            return (parent as DialogWindowProvider).window
        }
        parent = (parent as? View)?.parent
    }
    return null
}