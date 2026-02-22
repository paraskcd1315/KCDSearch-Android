package com.paraskcd.kcdsearch.ui.shared.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarInputField(
    query: String,
    onQueryChange: (String) -> Unit,
    searchBarState: SearchBarState,
    scope: CoroutineScope,
    placeholder: String,
) {
    val textFieldState = rememberTextFieldState(initialText = query)

    LaunchedEffect(Unit) {
        snapshotFlow { textFieldState.text.toString() }
            .collect { text ->
                if (text != query) {
                    onQueryChange(text)
                }
            }
    }

    LaunchedEffect(query) {
        if (textFieldState.text.toString() != query) {
            textFieldState.edit { replace(0, length, query) }
        }
    }

    SearchBarDefaults.InputField(
        textFieldState = textFieldState,
        searchBarState = searchBarState,
        onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                modifier = Modifier.fillMaxWidth().clearAndSetSemantics() {},
            )
        },
        leadingIcon = {},
        trailingIcon = null,
    )
}