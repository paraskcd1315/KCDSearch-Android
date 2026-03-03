package com.paraskcd.kcdsearch.ui.modules.search.components.searchTabs.contents.generalContent

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.composables.icons.heroicons.Heroicons
import com.composables.icons.heroicons.outline.ExclamationTriangle
import com.paraskcd.kcdsearch.ui.modules.search.components.appsAccordion.AppsAccordion
import com.paraskcd.kcdsearch.ui.modules.search.components.appsAccordion.AppsAccordionParams
import com.paraskcd.kcdsearch.ui.modules.search.components.appsAccordion.components.appListRow.AppListRow
import com.paraskcd.kcdsearch.ui.modules.search.components.appsAccordion.components.appListRow.AppListRowParams
import com.paraskcd.kcdsearch.ui.modules.search.components.contactsAccordion.ContactsAccordion
import com.paraskcd.kcdsearch.ui.modules.search.components.contactsAccordion.ContactsAccordionParams
import com.paraskcd.kcdsearch.ui.modules.search.components.contactsAccordion.components.contactListRow.ContactListRow
import com.paraskcd.kcdsearch.ui.modules.search.components.contactsAccordion.components.contactListRow.ContactListRowParams
import com.paraskcd.kcdsearch.ui.modules.search.components.infoboxAccordion.InfoboxAccordion
import com.paraskcd.kcdsearch.ui.modules.search.components.infoboxAccordion.InfoboxAccordionParams
import com.paraskcd.kcdsearch.ui.modules.search.components.searchResultSkeleton.SearchResultSkeleton
import com.paraskcd.kcdsearch.ui.modules.search.components.searchResultSkeleton.SearchResultSkeletonParams
import com.paraskcd.kcdsearch.ui.modules.search.components.webResultCard.WebResultCard
import com.paraskcd.kcdsearch.ui.modules.search.components.webResultCard.WebResultCardParams
import com.paraskcd.kcdsearch.ui.shared.components.listItemRow.ListItemRow
import com.paraskcd.kcdsearch.ui.shared.components.listItemRow.ListItemRowParams

fun LazyListScope.generalContent(params: GeneralContentParams) {
    val (
        isLoading,
        query,
        webResults,
        infoboxes,
        appResults,
        contactResults,
        hasMorePages,
        viewModel,
        context,
    ) = params

    if (isLoading && query.isNotBlank() && webResults.isEmpty() && infoboxes.isEmpty()) {
        items(5, key = { "skeleton_$it" }) {
            SearchResultSkeleton(
                params = SearchResultSkeletonParams(
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            )
        }
    } else {
        if (appResults.size > 4) {
            item(key = "apps_accordion") {
                AppsAccordion(
                    params = AppsAccordionParams(
                        appResults,
                        getAppIcon = { viewModel.getAppIcon(it) },
                        launchApp = { viewModel.launchApp(it) }
                    )
                )
            }
        } else {
            items(appResults, key = { it.packageName }) { app ->
                AppListRow(
                    params = AppListRowParams(
                        app,
                        modifier = Modifier.padding(bottom = 16.dp),
                        getAppIcon = { viewModel.getAppIcon(it) },
                        launchApp = { viewModel.launchApp(it) }
                    )
                )
            }
        }
        if (viewModel.contactRequiresPermission() && query.isNotBlank()) {
            item(key = "contact_permission") {
                ListItemRow(
                    params = ListItemRowParams(
                        label = "Allow contact access",
                        leadingContent = {
                            Icon(
                                imageVector = Heroicons.Outline.ExclamationTriangle,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )
                        },
                        onClick = {
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data =
                                        "package:${context.packageName}".toUri()
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                )
            }
        }
        if (contactResults.size > 4) {
            item(key = "contacts_accordion") {
                ContactsAccordion(
                    params = ContactsAccordionParams(
                        contactResults,
                        modifier = Modifier.padding(bottom = 16.dp),
                        onContactClick = {
                            viewModel.openContactDetails(
                                viewModel.getContactUriByNumber(it)
                            )
                        },
                        onCallClick = { viewModel.openDialer(it) },
                        onMessageClick = { viewModel.openMessages(it) },
                        onWhatsappClick = { viewModel.openWhatsApp(it) },
                        isWhatsappInstalled = viewModel.isWhatsappInstalled()
                    )
                )
            }
        } else {
            itemsIndexed(
                contactResults,
                key = { index, contact -> "contact_${index}_${contact.name}_${contact.number}"}
            ) { _, contactResult ->
                ContactListRow(
                    params = ContactListRowParams(
                        contactResult,
                        modifier = Modifier.padding(bottom = 16.dp),
                        onContactClick = { viewModel.openContactDetails(viewModel.getContactUriByNumber(contactResult.number)) },
                        onCallClick = { viewModel.openDialer(contactResult.number) },
                        onMessageClick = { viewModel.openMessages(contactResult.number) },
                        onWhatsAppClick = { viewModel.openWhatsApp(contactResult.number) },
                        isWhatsappInstalled = viewModel.isWhatsappInstalled()
                    )
                )
            }
        }
        itemsIndexed(
            infoboxes.filter { it.attributes.isNotEmpty() || !it.content.isNullOrBlank() || !it.imgSrc.isNullOrBlank() },
            key = { index, infobox -> "infobox_${index}_${infobox.title}_${infobox.infobox}_${infobox.engine}" }
        ) { _, infobox ->
            InfoboxAccordion(
                params = InfoboxAccordionParams(
                    infobox = infobox
                )
            )
        }
        itemsIndexed(
            webResults,
            key = { index, result -> "web_${index}_${result.url.orEmpty()}_${result.title.orEmpty()}" }
        ) { _, result ->
            WebResultCard(
                params = WebResultCardParams(
                    result = result,
                    modifier = Modifier.padding(bottom = 16.dp),
                    onUrlClick = viewModel::openUrl
                )
            )
        }
        if (isLoading && webResults.isNotEmpty()) {
            item(key = "loading_more") {
                SearchResultSkeleton(
                    params = SearchResultSkeletonParams(
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                )
            }
        }
        if (!hasMorePages && webResults.isNotEmpty()) {
            item(key = "no_more_results") {
                Text(
                    text = "No more results",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = 0.8f
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}