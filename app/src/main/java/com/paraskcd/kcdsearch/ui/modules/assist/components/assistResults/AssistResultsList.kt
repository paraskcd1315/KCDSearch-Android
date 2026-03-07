package com.paraskcd.kcdsearch.ui.modules.assist.components.assistResults

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.heroicons.Heroicons
import com.composables.icons.heroicons.outline.ChatBubbleBottomCenterText
import com.composables.icons.heroicons.outline.MagnifyingGlass
import com.composables.icons.heroicons.outline.Phone
import com.composables.icons.heroicons.outline.User
import com.paraskcd.kcdsearch.ui.shared.icons.FontAwesomeWhatsapp
import com.paraskcd.kcdsearch.constants.AssistConstants
import com.paraskcd.kcdsearch.model.SuggestionItem
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.suggestionSkeletonItem.SuggestionSkeletonItem
import com.paraskcd.kcdsearch.ui.shared.components.unifiedSearchBar.components.suggestionSkeletonItem.SuggestionSkeletonItemParams

private val CORNER = RoundedCornerShape(24.dp)

@Composable
fun AssistResultsList(params: AssistResultsListParams, modifier: Modifier = Modifier) {
    val containerAlpha = if (params.supportsBlur)
        AssistConstants.COMPONENT_ALPHA_WITH_BLUR
    else AssistConstants.COMPONENT_ALPHA_WITHOUT_BLUR

    val apps = params.suggestions.filterIsInstance<SuggestionItem.App>()
    val contacts = params.suggestions.filterIsInstance<SuggestionItem.Contact>()
    val textSuggestions = params.suggestions.filterIsInstance<SuggestionItem.Text>()
        .filter { !it.isHistory }

    Column(
        modifier = modifier
            .clip(CORNER)
            .background(
                MaterialTheme.colorScheme.surfaceBright.copy(alpha = containerAlpha)
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = AssistConstants.BORDER_ALPHA),
                CORNER
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            if (apps.isNotEmpty()) {
                item(key = "header_apps") {
                    SectionHeader(title = "Apps")
                }
                items(
                    items = apps,
                    key = { "app_${it.item.packageName}" }
                ) { app ->
                    val icon = params.getAppIcon(app.item.packageName)
                    ListItem(
                        headlineContent = { Text(app.item.label) },
                        supportingContent = { Text(app.item.packageName) },
                        leadingContent = {
                            if (icon != null) {
                                Image(
                                    bitmap = icon,
                                    contentDescription = app.item.label,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { params.onSuggestionClick(app) }
                    )
                }
            }
            if (contacts.isNotEmpty()) {
                item(key = "header_contacts") {
                    SectionHeader(title = "Contacts")
                }
                items(
                    items = contacts,
                    key = { "contact_${it.item.name}_${it.item.number}" }
                ) { contact ->
                    ListItem(
                        headlineContent = { Text(contact.item.name) },
                        supportingContent = { Text(contact.item.number) },
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
                        trailingContent = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { params.onCallClick(contact.item.number) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Heroicons.Outline.Phone, contentDescription = "Call", modifier = Modifier.size(20.dp))
                                }
                                IconButton(
                                    onClick = { params.onMessageClick(contact.item.number) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Heroicons.Outline.ChatBubbleBottomCenterText, contentDescription = "Message", modifier = Modifier.size(20.dp))
                                }
                                if (params.isWhatsappInstalled) {
                                    IconButton(
                                        onClick = { params.onWhatsAppClick(contact.item.number) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(FontAwesomeWhatsapp, contentDescription = "WhatsApp", modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { params.onSuggestionClick(contact) }
                    )
                }
            }
            if (textSuggestions.isNotEmpty()) {
                item(key = "header_suggestions") {
                    SectionHeader(title = "Suggestions")
                }
                items(
                    items = textSuggestions,
                    key = { "text_${it.value}" }
                ) { text ->
                    ListItem(
                        headlineContent = { Text(text.value) },
                        leadingContent = {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceContainerHigh
                            ) {
                                Icon(
                                    imageVector = Heroicons.Outline.MagnifyingGlass,
                                    contentDescription = null,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { params.onSuggestionClick(text) }
                    )
                }
            }
            if (params.isLoading) {
                val skeletonFractions = listOf(0.95f, 0.72f, 0.88f)
                items(
                    items = skeletonFractions,
                    key = { "skeleton_$it" }
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
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
