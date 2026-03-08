package com.paraskcd.kcdsearch.ui.activities.search.components.contactsAccordion.components.contactListRow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.request.ImageRequest
import com.composables.icons.heroicons.Heroicons
import com.composables.icons.heroicons.outline.ChatBubbleBottomCenterText
import com.composables.icons.heroicons.outline.Phone
import com.composables.icons.heroicons.outline.User
import com.paraskcd.kcdsearch.ui.shared.components.asyncImage.AsyncImageWithPlaceholder
import com.paraskcd.kcdsearch.ui.shared.components.asyncImage.AsyncImageWithPlaceholderParams
import com.paraskcd.kcdsearch.ui.shared.components.listItemRow.ListItemRow
import com.paraskcd.kcdsearch.ui.shared.components.listItemRow.ListItemRowParams
import com.paraskcd.kcdsearch.ui.shared.icons.FontAwesomeWhatsapp

@Composable
fun ContactListRow(params: ContactListRowParams) {
    val context = LocalContext.current
    val placeholder = @Composable {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Icon(
                imageVector = Heroicons.Outline.User,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }

    ListItemRow(
        params = ListItemRowParams(
            label = params.contact.name,
            leadingContent = {
                params.contact.photoUri?.let { uri ->
                    AsyncImageWithPlaceholder(
                        params = AsyncImageWithPlaceholderParams(
                            model = ImageRequest.Builder(context).data(uri).crossfade(true).build(),
                            contentDescription = params.contact.name,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            loadingContent = placeholder,
                            errorContent = placeholder
                        )
                    )
                } ?: run {
                    placeholder()
                }
            },
            onClick = params.onContactClick,
            modifier = params.modifier,
            trailingContent = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = params.onCallClick, modifier = Modifier.size(40.dp)) {
                        Icon(Heroicons.Outline.Phone, contentDescription = "Call", modifier = Modifier.size(24.dp))
                    }
                    IconButton(onClick = params.onMessageClick, modifier = Modifier.size(40.dp)) {
                        Icon(Heroicons.Outline.ChatBubbleBottomCenterText, contentDescription = "Message", modifier = Modifier.size(24.dp))
                    }
                    if (params.isWhatsappInstalled && params.onWhatsAppClick != null) {
                        IconButton(onClick = params.onWhatsAppClick, modifier = Modifier.size(40.dp)) {
                            Icon(FontAwesomeWhatsapp, contentDescription = "WhatsApp", modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        )
    )
}