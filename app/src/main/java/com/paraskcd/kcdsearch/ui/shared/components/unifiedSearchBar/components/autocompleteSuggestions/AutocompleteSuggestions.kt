package com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.autocompleteSuggestions

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.heroicons.Heroicons
import com.composables.icons.heroicons.outline.Clock
import com.composables.icons.heroicons.outline.MagnifyingGlass
import com.composables.icons.heroicons.outline.User
import com.paraskcd.kcdsearch.model.SuggestionItem
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.suggestionSkeletonItem.SuggestionSkeletonItem
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.suggestionSkeletonItem.SuggestionSkeletonItemParams
import com.paraskcd.kcdsearch.utils.extensionMethods.segmentedListItems

@Composable
fun AutocompleteSuggestions(
    params: AutocompleteSuggestionParams
) {
    val skeletonFractions = listOf(0.95f, 0.72f, 0.88f, 0.65f, 0.78f, 0.82f)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp)
    ) {
        segmentedListItems(
            items = params.suggestions,
            key = { index, suggestion ->
                when (suggestion) {
                    is SuggestionItem.Text -> "text_${index}_${suggestion.value}"
                    is SuggestionItem.App -> "app__${index}_${suggestion.item.packageName}"
                    is SuggestionItem.Contact -> "contact__${index}_${suggestion.item.name}_${suggestion.item.number}"
                }
            }
        ) { _, suggestion, _, _ ->
            when (suggestion) {
                is SuggestionItem.Text -> ListItem(
                    headlineContent = { Text(suggestion.value) },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    leadingContent = {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainerHigh
                        ) {
                            Icon(
                                imageVector = if (params.isHistory)
                                    Heroicons.Outline.Clock else Heroicons.Outline.MagnifyingGlass,
                                contentDescription = null,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { params.onSuggestionClick(suggestion) }
                )
                is SuggestionItem.App -> {
                    val icon = params.getAppIcon(suggestion.item.packageName)
                    ListItem(
                        headlineContent = { Text(suggestion.item.label) },
                        supportingContent = { Text(suggestion.item.packageName) },
                        leadingContent = {
                            if (icon != null) {
                                Image(
                                    bitmap = icon,
                                    contentDescription = suggestion.item.label,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { params.onSuggestionClick(suggestion) }
                    )
                }
                is SuggestionItem.Contact -> {
                    ListItem(
                        headlineContent = { Text(suggestion.item.name) },
                        supportingContent = { Text(suggestion.item.number) },
                        leadingContent = {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceContainerHigh
                            ) {
                                Icon(
                                    imageVector = Heroicons.Outline.User,
                                    contentDescription = null,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { params.onSuggestionClick(suggestion) }
                    )
                }
            }
        }
        if (params.isLoading) {
            items(
                items = skeletonFractions,
                key = { "skeleton_${it}" }
            ) { fraction ->
                SuggestionSkeletonItem(
                    params = SuggestionSkeletonItemParams(
                        textWidthFraction = fraction,
                        modifier = Modifier.fillMaxWidth()
                    )
                )
            }
        }
    }
}