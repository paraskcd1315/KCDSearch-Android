package com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.paraskcd.kcdsearch.model.SuggestionItem
import com.paraskcd.kcdsearch.ui.activities.assist.components.dialogContainer.DialogContainer
import com.paraskcd.kcdsearch.ui.activities.assist.components.dialogContainer.DialogContainerParams
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.components.appItem.AppItem
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.components.appItem.AppItemParams
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.components.assistResultSkeleton.AssistResultsSkeleton
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.components.contactItem.ContactItem
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.components.contactItem.ContactItemParams
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.components.searchActionItem.SearchActionItem
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.components.searchActionItem.SearchActionItemParams
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.components.sectionHeader.SectionHeader
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.components.sectionHeader.SectionHeaderParams
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.components.textItem.TextItem
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.components.textItem.TextItemParams

@Composable
fun AssistResultsList(params: AssistResultsListParams) {
    val apps = params.suggestions.filterIsInstance<SuggestionItem.App>()
    val contacts = params.suggestions.filterIsInstance<SuggestionItem.Contact>()
    val texts = params.suggestions.filterIsInstance<SuggestionItem.Text>()
    val searchActions = params.suggestions.filterIsInstance<SuggestionItem.SearchAction>()

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
                    items(items = apps, key = { "app_${it.item.packageName}" }) { app ->
                        AppItem(AppItemParams(app), params)
                    }
                }

                if (contacts.isNotEmpty()) {
                    item(key = "header_contacts") {
                        SectionHeader(params = SectionHeaderParams("Contacts"))
                    }
                    items(
                        items = contacts,
                        key = { "contact_${it.item.name}_${it.item.number}" }) { contact ->
                        ContactItem(ContactItemParams(contact), params)
                    }
                }

                if (texts.isNotEmpty()) {
                    item(key = "header_suggestions") {
                        SectionHeader(params = SectionHeaderParams("Suggestions"))
                    }
                    items(items = texts, key = { "text_${it.value}" }) { text ->
                        TextItem(TextItemParams(text), params)
                    }
                }

                if (searchActions.isNotEmpty()) {
                    item(key = "header_quick_search") {
                        SectionHeader(params = SectionHeaderParams("Quick search"))
                    }
                    items(items = searchActions, key = { "action_${it.item.packageName}" }) { action ->
                        SearchActionItem(SearchActionItemParams(action), params)
                    }
                }
            }
        }
    }
}