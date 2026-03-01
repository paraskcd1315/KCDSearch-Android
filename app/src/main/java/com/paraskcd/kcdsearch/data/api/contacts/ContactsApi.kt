package com.paraskcd.kcdsearch.data.api.contacts

import android.net.Uri
import com.paraskcd.kcdsearch.data.api.contacts.dataSources.ContactResult

interface ContactsApi {
    fun getContacts(): List<ContactResult>
    fun requiresPermission(): Boolean
    fun isWhatsappInstalled(): Boolean
    fun getContactUriByNumber(number: String): Uri?
    fun startObserving()
    fun stopObserving()
}