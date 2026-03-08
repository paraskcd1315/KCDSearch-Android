package com.paraskcd.kcdsearch.ui.activities.search.components.contactsAccordion.components.contactListRow

import androidx.compose.ui.Modifier
import com.paraskcd.kcdsearch.data.api.contacts.dataSources.ContactResult

data class ContactListRowParams(
    val contact: ContactResult,
    val modifier: Modifier = Modifier,
    val onContactClick: () -> Unit,
    val onCallClick: () -> Unit,
    val onMessageClick: () -> Unit,
    val onWhatsAppClick: (() -> Unit)?,
    val isWhatsappInstalled: Boolean
)
