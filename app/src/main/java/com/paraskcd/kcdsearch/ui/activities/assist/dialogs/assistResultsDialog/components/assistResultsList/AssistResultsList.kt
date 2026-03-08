package com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.heroicons.Heroicons
import com.composables.icons.heroicons.outline.ChatBubbleBottomCenterText
import com.composables.icons.heroicons.outline.Phone
import com.paraskcd.kcdsearch.model.SuggestionItem
import com.paraskcd.kcdsearch.ui.activities.assist.components.dialogContainer.DialogContainer
import com.paraskcd.kcdsearch.ui.activities.assist.components.dialogContainer.DialogContainerParams
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.components.assistResultSkeleton.AssistResultsSkeleton
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.components.sectionHeader.SectionHeader
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.components.sectionHeader.SectionHeaderParams
import com.paraskcd.kcdsearch.ui.shared.components.suggestionItems.SuggestionListItem
import com.paraskcd.kcdsearch.ui.shared.icons.FontAwesomeWhatsapp
import com.paraskcd.kcdsearch.utils.extensionMethods.segmentedListItems
import com.paraskcd.kcdsearch.utils.extensionMethods.toListItemParams

@Composable
fun AssistResultsList(params: AssistResultsListParams) {
    val apps = params.suggestions.filterIsInstance<SuggestionItem.App>()
    val contacts = params.suggestions.filterIsInstance<SuggestionItem.Contact>()
    val texts = params.suggestions.filterIsInstance<SuggestionItem.Text>()
    val searchActions = params.suggestions.filterIsInstance<SuggestionItem.SearchAction>()
    val containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f)

    val showSkeleton = params.isLoading && params.suggestions.isEmpty()

    DialogContainer(
        params = DialogContainerParams(
            supportsBlur = params.supportsBlur,
            modifier = params.modifier
        )
    ) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            if (showSkeleton) {
                item(key = "skeleton") {
                    AssistResultsSkeleton()
                }
            } else {
                if (apps.isNotEmpty()) {
                    item(key = "header_apps") {
                        SectionHeader(params = SectionHeaderParams("Apps"))
                    }
                    segmentedListItems(items = apps, key = { _, suggestion -> "app_${suggestion.item.packageName}" })
                    { _, suggestion, _, _ ->
                        SuggestionListItem(
                            params = suggestion.toListItemParams(
                                containerColor = containerColor,
                                onClick = { params.onSuggestionClick(suggestion) },
                                getAppIcon = params.getAppIcon
                            )
                        )
                    }
                }

                if (contacts.isNotEmpty()) {
                    item(key = "header_contacts") {
                        SectionHeader(params = SectionHeaderParams("Contacts"))
                    }
                    segmentedListItems(
                        items = contacts,
                        key = { index, suggestion -> "contact_${index}_${suggestion.item.name}_${suggestion.item.number}" }
                    )
                    { _, suggestion, _, _ ->
                        SuggestionListItem(
                            params = suggestion.toListItemParams(
                                containerColor = containerColor,
                                onClick = { params.onSuggestionClick(suggestion) },
                                getAppIcon = params.getAppIcon,
                                trailing = {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = { params.onCallClick(suggestion.item.number) },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(Heroicons.Outline.Phone,
                                                contentDescription = "Call",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = { params.onMessageClick(suggestion.item.number) },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                Heroicons.Outline.ChatBubbleBottomCenterText,
                                                contentDescription = "Message",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        if (params.isWhatsappInstalled) {
                                            IconButton(
                                                onClick = { params.onWhatsAppClick(suggestion.item.number) },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    FontAwesomeWhatsapp,
                                                    contentDescription = "WhatsApp",
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        )
                    }
                }

                if (texts.isNotEmpty()) {
                    item(key = "header_suggestions") {
                        SectionHeader(params = SectionHeaderParams("Suggestions"))
                    }
                    segmentedListItems(
                        items = texts,
                        key = { index, suggestion -> "texts_${index}_${suggestion.value}" }
                    )
                    { _, suggestion, _, _ ->
                        SuggestionListItem(
                            params = suggestion.toListItemParams(
                                containerColor = containerColor,
                                onClick = { params.onSuggestionClick(suggestion) },
                                getAppIcon = params.getAppIcon
                            )
                        )
                    }
                }

                if (searchActions.isNotEmpty()) {
                    item(key = "header_quick_search") {
                        SectionHeader(params = SectionHeaderParams("Quick search"))
                    }
                    segmentedListItems(
                        items = searchActions,
                        key = { index, suggestion -> "texts_${index}_${suggestion.item.packageName}" }
                    )
                    { _, suggestion, _, _ ->
                        SuggestionListItem(
                            params = suggestion.toListItemParams(
                                containerColor = containerColor,
                                onClick = { params.onSuggestionClick(suggestion) },
                                getAppIcon = params.getAppIcon
                            )
                        )
                    }
                }
            }
        }
    }
}