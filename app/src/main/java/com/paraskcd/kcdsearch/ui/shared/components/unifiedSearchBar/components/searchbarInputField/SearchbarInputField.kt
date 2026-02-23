package com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.searchbarInputField

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.composables.icons.heroicons.Heroicons
import com.composables.icons.heroicons.outline.MagnifyingGlass
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarInputField(
    params: SearchbarInputFieldParams
) {
    val textFieldState = rememberTextFieldState(initialText = params.query)

    LaunchedEffect(Unit) {
        snapshotFlow { textFieldState.text.toString() }
            .collect { text ->
                if (text != params.query) {
                    params.onQueryChange(text)
                }
            }
    }

    LaunchedEffect(params.query) {
        if (textFieldState.text.toString() != params.query) {
            textFieldState.edit { replace(0, length, params.query) }
        }
    }

    SearchBarDefaults.InputField(
        textFieldState = textFieldState,
        searchBarState = params.searchBarState,
        onSearch = { params.scope.launch { params.searchBarState.animateToCollapsed() } },
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = params.placeholder,
                modifier = Modifier.fillMaxWidth().clearAndSetSemantics() {},
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Heroicons.Outline.MagnifyingGlass,
                contentDescription = null,
            )
        },
        trailingIcon = null,
    )
}