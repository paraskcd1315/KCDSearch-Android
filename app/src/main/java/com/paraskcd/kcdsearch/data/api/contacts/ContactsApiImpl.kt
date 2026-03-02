package com.paraskcd.kcdsearch.data.api.contacts

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import com.paraskcd.kcdsearch.constants.GlobalConstants.WHATSAPP_PACKAGES
import com.paraskcd.kcdsearch.data.api.apps.InstalledAppsApi
import com.paraskcd.kcdsearch.data.api.contacts.dataSources.ContactResult
import com.paraskcd.kcdsearch.utils.extensionMethods.normalizePhoneNumber
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactsApiImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val installedAppsApi: InstalledAppsApi
): ContactsApi {
    private var cachedContacts: List<ContactResult>? = null
    private val contactObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(self: Boolean) {
            cachedContacts = null
        }
    }
    private var isObserving = false

    override fun getContacts(): List<ContactResult> {
        if (requiresPermission()) return emptyList()
        if (cachedContacts == null) cachedContacts = loadAllContacts()
        return cachedContacts!!
    }

    override fun requiresPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) !=
                PackageManager.PERMISSION_GRANTED

    override fun isWhatsappInstalled(): Boolean =
        installedAppsApi.getInstalledApps()
            .any { it.packageName in WHATSAPP_PACKAGES }

    override fun getContactUriByNumber(number: String): Uri? {
        if (requiresPermission()) return null
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY
        )
        val normalized = number.replace("[^\\d+]".toRegex(), "")
        val selection = "${ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER} = ?"
        val selectionArgs = arrayOf(normalized)
        val cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val contactId = it.getLong(0)
                val lookupKey = it.getString(1)
                return ContactsContract.Contacts.getLookupUri(contactId, lookupKey)
            }
        }
        return null
    }

    override fun startObserving() {
        if (isObserving || requiresPermission()) return
        context.contentResolver.registerContentObserver(
            ContactsContract.Contacts.CONTENT_URI,
            true,
            contactObserver
        )
        isObserving = true
    }

    override fun stopObserving() {
        if (!isObserving) return
        try {
            context.contentResolver.unregisterContentObserver(contactObserver)
        } catch (_: Exception) {}
        isObserving = false
    }

    private fun loadAllContacts(): List<ContactResult> {
        val results = mutableListOf<ContactResult>()
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        )
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val photoIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
            while (cursor.moveToNext()) {
                results.add(
                    ContactResult(
                        name = cursor.getString(nameIdx),
                        number = cursor.getString(numIdx),
                        photoUri = cursor.getString(photoIdx)
                    )
                )
            }
        }
        return deduplicateByNumber(results)
    }

    private fun deduplicateByNumber(contacts: List<ContactResult>): List<ContactResult> =
        contacts
            .groupBy { it.number.normalizePhoneNumber() }
            .values
            .map { group -> group.maxByOrNull { c -> c.photoUri != null } ?: group.first() }
}