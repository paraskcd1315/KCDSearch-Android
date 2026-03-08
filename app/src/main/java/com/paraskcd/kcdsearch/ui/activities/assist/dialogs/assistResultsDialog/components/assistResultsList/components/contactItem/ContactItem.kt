package com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.components.contactItem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composables.icons.heroicons.Heroicons
import com.composables.icons.heroicons.outline.ChatBubbleBottomCenterText
import com.composables.icons.heroicons.outline.Phone
import com.composables.icons.heroicons.outline.User
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.AssistResultsListParams
import com.paraskcd.kcdsearch.ui.shared.components.iconCircle.IconCircle
import com.paraskcd.kcdsearch.ui.shared.components.iconCircle.IconCircleParams
import com.paraskcd.kcdsearch.ui.shared.icons.FontAwesomeWhatsapp

@Composable
fun ContactItem(params: ContactItemParams, parentParams: AssistResultsListParams) {
    ListItem(
        headlineContent = { Text(params.contact.item.name) },
        supportingContent = { Text(params.contact.item.number) },
        leadingContent = {
            IconCircle(
                IconCircleParams(Heroicons.Outline.User)
            )
        },
        trailingContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { parentParams.onCallClick(params.contact.item.number) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Heroicons.Outline.Phone, contentDescription = "Call", modifier = Modifier.size(20.dp))
                }
                IconButton(
                    onClick = { parentParams.onMessageClick(params.contact.item.number) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Heroicons.Outline.ChatBubbleBottomCenterText, contentDescription = "Message", modifier = Modifier.size(20.dp))
                }
                if (parentParams.isWhatsappInstalled) {
                    IconButton(
                        onClick = { parentParams.onWhatsAppClick(params.contact.item.number) },
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
            .clickable { parentParams.onSuggestionClick(params.contact) }
    )
}