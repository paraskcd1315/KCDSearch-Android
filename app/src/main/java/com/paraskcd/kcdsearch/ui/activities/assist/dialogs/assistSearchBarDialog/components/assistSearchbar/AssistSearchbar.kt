package com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistSearchBarDialog.components.assistSearchbar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.composables.icons.heroicons.Heroicons
import com.composables.icons.heroicons.outline.MagnifyingGlass
import com.composables.icons.heroicons.outline.XMark
import com.paraskcd.kcdsearch.ui.activities.assist.components.dialogContainer.DialogContainer
import com.paraskcd.kcdsearch.ui.activities.assist.components.dialogContainer.DialogContainerParams
import kotlinx.coroutines.android.awaitFrame

@Composable
fun AssistSearchbar(params: AssistSearchbarParams) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        awaitFrame()
        awaitFrame()
        focusRequester.requestFocus()
    }

    DialogContainer(
        params = DialogContainerParams(
            supportsBlur = params.supportsBlur,
            modifier = params.modifier
        )
    ) {
        TextField(
            value = params.query,
            onValueChange = params.onQueryChange,
            placeholder = { Text("Search...") },
            singleLine = true,
            leadingIcon = {
                Icon(
                    Heroicons.Outline.MagnifyingGlass,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (params.query.isNotEmpty()) {
                    IconButton(onClick = params.onClear) {
                        Icon(Heroicons.Outline.XMark, contentDescription = "Clear")
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = { params.onSearchSubmit(params.query) }
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .focusRequester(focusRequester)
        )
    }
}