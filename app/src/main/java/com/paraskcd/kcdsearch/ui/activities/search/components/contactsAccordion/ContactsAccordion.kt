package com.paraskcd.kcdsearch.ui.activities.search.components.contactsAccordion

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paraskcd.kcdsearch.ui.activities.search.components.contactsAccordion.components.contactListRow.ContactListRow
import com.paraskcd.kcdsearch.ui.activities.search.components.contactsAccordion.components.contactListRow.ContactListRowParams
import com.paraskcd.kcdsearch.ui.shared.components.expandableAccordionSection.ExpandableAccordionSection
import com.paraskcd.kcdsearch.ui.shared.components.expandableAccordionSection.ExpandableAccordionSectionParams

@Composable
fun ContactsAccordion(params: ContactsAccordionParams) {
    ExpandableAccordionSection(
        params = ExpandableAccordionSectionParams(
            title = "Contacts",
            initiallyExpanded = false,
            modifier = Modifier.padding(bottom = 16.dp),
            contentPaddingValues = PaddingValues(top = 12.dp)
        )
    ) {
        params.contactResults.forEach { contactResult ->
            ContactListRow(
                params = ContactListRowParams(
                    contactResult,
                    onContactClick = { params.onContactClick(contactResult.number) },
                    onCallClick = { params.onCallClick(contactResult.number) },
                    onMessageClick = { params.onMessageClick(contactResult.number) },
                    onWhatsAppClick = { params.onWhatsappClick(contactResult.number) },
                    isWhatsappInstalled = params.isWhatsappInstalled
                )
            )
        }
    }
}