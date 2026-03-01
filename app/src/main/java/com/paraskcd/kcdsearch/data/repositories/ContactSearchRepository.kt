package com.paraskcd.kcdsearch.data.repositories

import com.paraskcd.kcdsearch.data.api.contacts.ContactsApi
import com.paraskcd.kcdsearch.data.api.contacts.dataSources.ContactResult
import com.paraskcd.kcdsearch.data.dtos.ContactSearchRequestDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactSearchRepository @Inject constructor(
    private val contactsApi: ContactsApi
) {
    fun search(contactSearchRequestDto: ContactSearchRequestDto): List<ContactResult> {
        val q = contactSearchRequestDto.query.trim().lowercase()
        if (q.isEmpty()) return emptyList()

        return contactsApi.getContacts()
            .filter { it.name.lowercase().contains(q) }
    }

    fun requiresPermission(): Boolean = contactsApi.requiresPermission()

    fun isWhatsappInstalled(): Boolean = contactsApi.isWhatsappInstalled()

    fun getContactUriByNumber(number: String) = contactsApi.getContactUriByNumber(number)
}