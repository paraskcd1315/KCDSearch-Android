package com.paraskcd.kcdsearch.ui.modules.search.components.contactsAccordion

import androidx.compose.ui.Modifier
import com.paraskcd.kcdsearch.data.api.contacts.dataSources.ContactResult

data class ContactsAccordionParams(
    val contactResults: List<ContactResult>,
    val modifier: Modifier = Modifier,
    val onContactClick: (contactNumber: String) -> Unit,
    val onCallClick: (contactNumber: String) -> Unit,
    val onMessageClick: (contactNumber: String) -> Unit,
    val onWhatsappClick: (contactNumber: String) -> Unit,
    val isWhatsappInstalled: Boolean
)