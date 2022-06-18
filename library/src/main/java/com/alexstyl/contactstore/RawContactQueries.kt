package com.alexstyl.contactstore

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import com.alexstyl.contactstore.utils.iterate
import com.alexstyl.contactstore.utils.runQuery

internal class RawContactQueries(
    private val contentResolver: ContentResolver
) {
    fun fetchRawContacts(contact: Contact): List<RawContact> {
        var rawContact: RawContact? = null
        var currentRawContactId: Long = -1
        val rawContacts = mutableListOf<RawContact>()

        val contentUri = entityUri(contact) ?: return emptyList()

        contentResolver.runQuery(
            contentUri = contentUri,
            projection = ContactQuery.COLUMNS,
            sortOrder = ContactsContract.Contacts.Entity.RAW_CONTACT_ID
        ).iterate { cursor ->
            val rawContactId = cursor.getLong(ContactQuery.RAW_CONTACT_ID)
            if (currentRawContactId != rawContactId) {
                currentRawContactId = rawContactId
                rawContact = RawContact(loadRawContactValues(cursor))
                rawContacts.add(rawContact!!)
            }
            if (!cursor.isNull(ContactQuery.DATA_ID)) {
                val data: ContentValues = loadDataValues(cursor)
                rawContact!!.addDataItemValues(data)
            }
        }
        return rawContacts.toList()
    }

    private fun entityUri(forContact: Contact): Uri? {
        val contactId = forContact.contactId
        val lookupKey = forContact.lookupKey ?: return null

        val contactUri = ensureIsContactUri(
            resolver = contentResolver,
            uri = ContactsContract.Contacts.getLookupUri(contactId, lookupKey.value)
        )
        return Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Entity.CONTENT_DIRECTORY)
    }

    private fun loadDataValues(cursor: Cursor): ContentValues {
        val cv = ContentValues()
        cv.put(ContactsContract.Data._ID, cursor.getLong(ContactQuery.DATA_ID))
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA1)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA2)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA3)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA4)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA5)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA6)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA7)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA8)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA9)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA10)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA11)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA12)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA13)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA14)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA15)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA_SYNC1)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA_SYNC2)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA_SYNC3)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA_SYNC4)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA_VERSION)
        cursorColumnToContentValues(cursor, cv, ContactQuery.IS_PRIMARY)
        cursorColumnToContentValues(cursor, cv, ContactQuery.IS_SUPERPRIMARY)
        cursorColumnToContentValues(cursor, cv, ContactQuery.MIMETYPE)
        cursorColumnToContentValues(cursor, cv, ContactQuery.GROUP_SOURCE_ID)
        cursorColumnToContentValues(cursor, cv, ContactQuery.CHAT_CAPABILITY)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cursorColumnToContentValues(cursor, cv, ContactQuery.CARRIER_PRESENCE)
        }
        return cv
    }

    private fun loadRawContactValues(cursor: Cursor): ContentValues {
        val cv = ContentValues()
        cv.put(ContactsContract.RawContacts._ID, cursor.getLong(ContactQuery.RAW_CONTACT_ID))
        cursorColumnToContentValues(cursor, cv, ContactQuery.ACCOUNT_NAME)
        cursorColumnToContentValues(cursor, cv, ContactQuery.ACCOUNT_TYPE)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA_SET)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DIRTY)
        cursorColumnToContentValues(cursor, cv, ContactQuery.VERSION)
        cursorColumnToContentValues(cursor, cv, ContactQuery.SOURCE_ID)
        cursorColumnToContentValues(cursor, cv, ContactQuery.SYNC1)
        cursorColumnToContentValues(cursor, cv, ContactQuery.SYNC2)
        cursorColumnToContentValues(cursor, cv, ContactQuery.SYNC3)
        cursorColumnToContentValues(cursor, cv, ContactQuery.SYNC4)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DELETED)
        cursorColumnToContentValues(cursor, cv, ContactQuery.CONTACT_ID)
        cursorColumnToContentValues(cursor, cv, ContactQuery.STARRED)
        return cv
    }

    private fun cursorColumnToContentValues(
        cursor: Cursor, values: ContentValues, index: Int
    ) {
        when (cursor.getType(index)) {
            Cursor.FIELD_TYPE_NULL -> {}
            Cursor.FIELD_TYPE_INTEGER -> values.put(
                ContactQuery.COLUMNS[index],
                cursor.getLong(index)
            )
            Cursor.FIELD_TYPE_STRING -> values.put(
                ContactQuery.COLUMNS[index],
                cursor.getString(index)
            )
            Cursor.FIELD_TYPE_BLOB -> values.put(ContactQuery.COLUMNS[index], cursor.getBlob(index))
            else -> throw IllegalStateException("Invalid or unhandled data type")
        }
    }

    private fun ensureIsContactUri(resolver: ContentResolver, uri: Uri): Uri {
        val authority = uri.authority

        // Current Style Uri?
        if (ContactsContract.AUTHORITY == authority) {
            val type = resolver.getType(uri)
            // Contact-Uri? Good, return it
            if (ContactsContract.Contacts.CONTENT_ITEM_TYPE == type) {
                return uri
            }

            // RawContact-Uri? Transform it to ContactUri
            if (ContactsContract.RawContacts.CONTENT_ITEM_TYPE == type) {
                val rawContactId = ContentUris.parseId(uri)
                return ContactsContract.RawContacts.getContactLookupUri(
                    resolver,
                    ContentUris.withAppendedId(
                        ContactsContract.RawContacts.CONTENT_URI,
                        rawContactId
                    )
                )
            }
            throw IllegalArgumentException("uri format is unknown")
        }

        // Legacy Style? Convert to RawContact
        val OBSOLETE_AUTHORITY = android.provider.Contacts.AUTHORITY
        if (OBSOLETE_AUTHORITY == authority) {
            // Legacy Format. Convert to RawContact-Uri and then lookup the contact
            val rawContactId = ContentUris.parseId(uri)
            return ContactsContract.RawContacts.getContactLookupUri(
                resolver,
                ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, rawContactId)
            )
        }
        throw IllegalArgumentException("uri authority is unknown")
    }
}
