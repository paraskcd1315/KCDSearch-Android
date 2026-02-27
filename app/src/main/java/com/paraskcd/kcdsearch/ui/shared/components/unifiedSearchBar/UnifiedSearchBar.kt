package com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar

import android.view.View
import android.view.ViewParent
import android.view.Window
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.paraskcd.kcdsearch.ui.shared.components.skeleton.Skeleton
import com.paraskcd.kcdsearch.ui.shared.components.skeleton.SkeletonParams
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.autocompleteSuggestions.AutocompleteSuggestionParams
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.autocompleteSuggestions.AutocompleteSuggestions
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.searchbarInputField.SearchBarInputField
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.searchbarInputField.SearchbarInputFieldParams
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedSearchBar(params: UnifiedSearchBarParams) {
    val searchbarInputParams = SearchbarInputFieldParams(
        query = params.query,
        onQueryChange = params.onQueryChange,
        searchBarState = params.searchBarState,
        scope = params.scope,
        placeholder = params.placeholder,
        onSearchSubmit = params.onSuggestionClick,
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

        AnimatedVisibility(
            visible = params.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(5) {
                    Skeleton(
                        params = SkeletonParams(
                            fillMaxWidth = true,
                            height = 28.dp,
                            clip = RoundedCornerShape(16.dp)
                        ),
                        modifier = Modifier.skeletonModifiers()
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = params.suggestions.isNotEmpty() && !params.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            AutocompleteSuggestions(
                params = AutocompleteSuggestionParams(
                    suggestions = params.suggestions,
                    onSuggestionClick = { suggestion ->
                        params.scope.launch {
                            params.searchBarState.animateToCollapsed()
                            params.onSuggestionClick(suggestion)
                        }
                    }
                )
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