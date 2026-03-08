package com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.autocompleteSuggestions

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composables.icons.heroicons.Heroicons
import com.composables.icons.heroicons.outline.ArrowTopRightOnSquare
import com.composables.icons.heroicons.outline.Clock
import com.composables.icons.heroicons.outline.MagnifyingGlass
import com.composables.icons.heroicons.outline.User
import com.paraskcd.kcdsearch.model.SuggestionItem
import com.paraskcd.kcdsearch.ui.shared.components.iconCircle.IconCircle
import com.paraskcd.kcdsearch.ui.shared.components.iconCircle.IconCircleParams
import com.paraskcd.kcdsearch.ui.shared.components.suggestionItems.SuggestionListItem
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.suggestionSkeletonItem.SuggestionSkeletonItem
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.suggestionSkeletonItem.SuggestionSkeletonItemParams
import com.paraskcd.kcdsearch.utils.extensionMethods.segmentedListItems
import com.paraskcd.kcdsearch.utils.extensionMethods.toListItemParams

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
                    is SuggestionItem.SearchAction -> "search_action_${index}_${suggestion.item.packageName}"
                }
            }
        ) { _, suggestion, _, _ ->
            SuggestionListItem(
                params = suggestion.toListItemParams(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    onClick = { params.onSuggestionClick(suggestion) },
                    getAppIcon = params.getAppIcon,
                    textIcon = if (params.isHistory) Heroicons.Outline.Clock else Heroicons.Outline.MagnifyingGlass
                )
            )
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