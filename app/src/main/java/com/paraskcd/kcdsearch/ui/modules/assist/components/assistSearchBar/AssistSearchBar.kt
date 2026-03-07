package com.paraskcd.kcdsearch.ui.modules.assist.components.assistSearchBar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import com.composables.icons.heroicons.Heroicons
import com.composables.icons.heroicons.outline.MagnifyingGlass
import com.composables.icons.heroicons.outline.XMark
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.paraskcd.kcdsearch.constants.AssistConstants
import kotlinx.coroutines.android.awaitFrame

@Composable
fun AssistSearchBar(params: AssistSearchBarParams) {
    val focusRequester = remember { FocusRequester() }

    val containerAlpha = if (params.supportsBlur)
        AssistConstants.COMPONENT_ALPHA_WITH_BLUR
    else
        AssistConstants.COMPONENT_ALPHA_WITHOUT_BLUR

    LaunchedEffect(Unit) {
        awaitFrame()
        awaitFrame()
        focusRequester.requestFocus()
    }

    TextField(
        value = params.query,
        onValueChange = params.onQueryChange,
        modifier = params.modifier
            .focusRequester(focusRequester)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = AssistConstants.BORDER_ALPHA),
                shape = RoundedCornerShape(100.dp)
            ),
        shape = RoundedCornerShape(100.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = { params.onSearchSubmit(params.query) }
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceBright.copy(alpha = containerAlpha),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceBright.copy(alpha = containerAlpha),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.onSurface,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface
        ),
        placeholder = { Text(params.placeholder) },
        leadingIcon = {
            Icon(
                imageVector = Heroicons.Outline.MagnifyingGlass,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 24.dp, end = 8.dp)
            )
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = params.query.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(
                    onClick = params.onClear,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 16.dp)
                        .size(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = MaterialTheme.colorScheme.outline.copy(alpha = AssistConstants.BORDER_ALPHA),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Heroicons.Outline.XMark,
                            contentDescription = "Clear query",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    )
}
