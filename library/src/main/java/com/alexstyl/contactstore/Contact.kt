@file:JvmName("ContactKt")

package com.alexstyl.contactstore

import android.content.ContentUris
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.Contacts
import android.provider.ContactsContract.Contacts.Photo
import android.provider.ContactsContract.FullNameStyle
import android.provider.ContactsContract.PhoneticNameStyle
import com.alexstyl.contactstore.ContactColumn.EVENTS
import com.alexstyl.contactstore.ContactColumn.GROUP_MEMBERSHIPS
import com.alexstyl.contactstore.ContactColumn.IMAGE
import com.alexstyl.contactstore.ContactColumn.MAILS
import com.alexstyl.contactstore.ContactColumn.NAMES
import com.alexstyl.contactstore.ContactColumn.NICKNAME
import com.alexstyl.contactstore.ContactColumn.NOTE
import com.alexstyl.contactstore.ContactColumn.ORGANIZATION
import com.alexstyl.contactstore.ContactColumn.PHONES
import com.alexstyl.contactstore.ContactColumn.POSTAL_ADDRESSES
import com.alexstyl.contactstore.ContactColumn.WEB_ADDRESSES

interface Contact {
    val contactId: Long
    val displayName: String?

    /**
     * Requires: [ContactColumn.NAMES]
     */
    val prefix: String?

    /**
     * Requires: [ContactColumn.NAMES]
     */
    val firstName: String?

    /**
     * Requires: [ContactColumn.NAMES]
     */
    val middleName: String?

    /**
     * Requires: [ContactColumn.NAMES]
     */
    val lastName: String?

    /**
     * Requires: [ContactColumn.NAMES]
     */
    val suffix: String?

    /**
     * Requires: [ContactColumn.NAMES]
     */
    val phoneticFirstName: String?

    /**
     * Requires: [ContactColumn.NAMES]
     */
    val phoneticMiddleName: String?

    /**
     * Requires: [ContactColumn.NAMES]
     */
    val phoneticLastName: String?

    /**
     * Requires: [ContactColumn.NICKNAME]
     */
    val nickname: String?

    /**
     * Requires: [ContactColumn.GROUP_MEMBERSHIPS]
     */
    val groups: List<GroupMembership>

    /**
     * Requires: [ContactColumn.NAMES]
     *
     * See [ContactsContract.FullNameStyle]
     */
    val fullNameStyle: Int

    /**
     * Requires: [ContactColumn.NAMES]
     *
     * See [ContactsContract.PhoneticNameStyle]
     */
    val phoneticNameStyle: Int

    /**
     * Requires: [ContactColumn.IMAGE]
     *
     * *NOTE*: If you need the contact image for UI purposes, you probably need [Contact.imageUri] instead.
     * [ImageData] contains the raw [ByteArray] of the image, which might consume a lot of memory.
     */
    val imageData: ImageData?

    /**
     * Requires: [ContactColumn.PHONES]
     */
    val phones: List<LabeledValue<PhoneNumber>>

    /**
     * Requires: [ContactColumn.MAILS]
     */
    val mails: List<LabeledValue<MailAddress>>

    /**
     * Requires: [ContactColumn.EVENTS]
     */
    val events: List<LabeledValue<EventDate>>

    /**
     * Requires: [ContactColumn.POSTAL_ADDRESSES]
     */
    val postalAddresses: List<LabeledValue<PostalAddress>>

    /**
     * Requires: [ContactColumn.WEB_ADDRESSES]
     */
    val webAddresses: List<LabeledValue<WebAddress>>

    /**
     * Requires: [ContactColumn.NOTE]
     */
    val note: Note?

    /**
     * Requires: [ContactColumn.ORGANIZATION]
     */
    val organization: String?

    /**
     * Requires: [ContactColumn.ORGANIZATION]
     */
    val jobTitle: String?
    val isStarred: Boolean
    fun containsColumn(column: ContactColumn): Boolean
    val columns: List<ContactColumn>
}

/**
 * Creates a copy of the Contact that can have its properties modified.
 *
 * Modifying the properties of the contact will not affect the stored contact of the device.
 * See [ContactStore] to learn how to persist your changes.
 */
fun Contact.mutableCopy(): MutableContact {
    return MutableContact(
        contactId = contactId,
        firstName = if (containsColumn(NAMES)) firstName else null,
        organization = if (containsColumn(ORGANIZATION)) organization else null,
        jobTitle = if (containsColumn(ORGANIZATION)) jobTitle else null,
        lastName = if (containsColumn(NAMES)) lastName else null,
        isStarred = isStarred,
        imageData = if (containsColumn(IMAGE)) imageData else null,
        phones = if (containsColumn(PHONES)) phones.toMutableList() else mutableListOf(),
        mails = if (containsColumn(MAILS)) mails.toMutableList() else mutableListOf(),
        events = if (containsColumn(EVENTS)) events.toMutableList() else mutableListOf(),
        postalAddresses = if (containsColumn(POSTAL_ADDRESSES))
            postalAddresses.toMutableList()
        else
            mutableListOf(),
        webAddresses = if (containsColumn(WEB_ADDRESSES))
            webAddresses.toMutableList()
        else
            mutableListOf(),
        note = if (containsColumn(NOTE)) note else null,
        columns = columns,
        middleName = if (containsColumn(NAMES)) middleName else null,
        prefix = if (containsColumn(NAMES)) prefix else null,
        suffix = if (containsColumn(NAMES)) suffix else null,
        phoneticNameStyle = if (containsColumn(NAMES)) phoneticNameStyle else PhoneticNameStyle.UNDEFINED,
        phoneticFirstName = if (containsColumn(NAMES)) phoneticFirstName else null,
        phoneticLastName = if (containsColumn(NAMES)) phoneticLastName else null,
        phoneticMiddleName = if (containsColumn(NAMES)) phoneticMiddleName else null,
        groups = if (containsColumn(GROUP_MEMBERSHIPS)) groups.toMutableList() else mutableListOf(),
        fullNameStyle = if (containsColumn(NAMES)) phoneticNameStyle else FullNameStyle.UNDEFINED,
        nickname = if (containsColumn(NICKNAME)) nickname else null,
    )
}

/**
 * Creates a [Uri] pointing to the image assigned to the contact
 */
val Contact.imageUri: Uri
    get() {
        val contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId)
        return Uri.withAppendedPath(contactUri, Photo.CONTENT_DIRECTORY)
    }
