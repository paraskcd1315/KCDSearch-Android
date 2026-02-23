package com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar

import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
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
import com.paraskcd.kcdsearch.ui.shared.modifiers.shimmerLoading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedSearchBar(params: UnifiedSearchBarParams) {
    val searchbarInputParams = SearchbarInputFieldParams(
        query = params.query,
        onQueryChange = params.onQueryChange,
        searchBarState = params.searchBarState,
        scope = params.scope,
        placeholder = params.placeholder
    )

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

        if (params.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().height(8.dp).padding(horizontal = 16.dp).shimmerLoading()
            )
        }
        AutocompleteSuggestions(
            params = AutocompleteSuggestionParams(
                suggestions = params.suggestions,
                onSuggestionClick = params.onSuggestionClick
            )
        )
    }
}

private fun findDialogWindow(view: View): android.view.Window? {
    var parent: android.view.ViewParent? = view.parent
    while (parent != null) {
        if (parent is DialogWindowProvider) {
            return (parent as DialogWindowProvider).window
        }
        parent = (parent as? View)?.parent
    }
    return null
}