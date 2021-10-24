@file:JvmName("ContactKt")

package com.alexstyl.contactstore

import android.content.ContentUris
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.Contacts
import android.provider.ContactsContract.Contacts.Photo
import android.provider.ContactsContract.FullNameStyle
import android.provider.ContactsContract.PhoneticNameStyle
import com.alexstyl.contactstore.ContactColumn.Events
import com.alexstyl.contactstore.ContactColumn.GroupMemberships
import com.alexstyl.contactstore.ContactColumn.Image
import com.alexstyl.contactstore.ContactColumn.LinkedAccountValues
import com.alexstyl.contactstore.ContactColumn.Mails
import com.alexstyl.contactstore.ContactColumn.Names
import com.alexstyl.contactstore.ContactColumn.Nickname
import com.alexstyl.contactstore.ContactColumn.Note
import com.alexstyl.contactstore.ContactColumn.Organization
import com.alexstyl.contactstore.ContactColumn.Phones
import com.alexstyl.contactstore.ContactColumn.PostalAddresses
import com.alexstyl.contactstore.ContactColumn.WebAddresses

interface Contact {
    val contactId: Long
    val displayName: String?

    /**
     * Requires: [ContactColumn.Names]
     */
    val prefix: String?

    /**
     * Requires: [ContactColumn.Names]
     */
    val firstName: String?

    /**
     * Requires: [ContactColumn.Names]
     */
    val middleName: String?

    /**
     * Requires: [ContactColumn.Names]
     */
    val lastName: String?

    /**
     * Requires: [ContactColumn.Names]
     */
    val suffix: String?

    /**
     * Requires: [ContactColumn.Names]
     */
    val phoneticFirstName: String?

    /**
     * Requires: [ContactColumn.Names]
     */
    val phoneticMiddleName: String?

    /**
     * Requires: [ContactColumn.Names]
     */
    val phoneticLastName: String?

    /**
     * Requires: [ContactColumn.Nickname]
     */
    val nickname: String?

    /**
     * Requires: [ContactColumn.GroupMemberships]
     */
    val groups: List<GroupMembership>

    /**
     * Requires: [ContactColumn.Names]
     *
     * See [ContactsContract.FullNameStyle]
     */
    val fullNameStyle: Int

    /**
     * Requires: [ContactColumn.Names]
     *
     * See [ContactsContract.PhoneticNameStyle]
     */
    val phoneticNameStyle: Int

    /**
     * Requires: [ContactColumn.Image]
     *
     * *NOTE*: If you need the contact image for UI purposes, you probably need [Contact.imageUri] instead.
     * [ImageData] contains the raw [ByteArray] of the image, which might consume a lot of memory.
     */
    val imageData: ImageData?

    /**
     * Requires: [ContactColumn.Phones]
     */
    val phones: List<LabeledValue<PhoneNumber>>

    /**
     * Requires: [ContactColumn.Mails]
     */
    val mails: List<LabeledValue<MailAddress>>

    /**
     * Requires: [ContactColumn.Events]
     */
    val events: List<LabeledValue<EventDate>>

    /**
     * Requires: [ContactColumn.PostalAddresses]
     */
    val postalAddresses: List<LabeledValue<PostalAddress>>

    /**
     * Requires: [ContactColumn.WebAddresses]
     */
    val webAddresses: List<LabeledValue<WebAddress>>

    /**
     * Requires : [ContactColumn.LinkedAccountValues]
     */
    val linkedAccountValues: List<LinkedAccountValue>

    /**
     * Requires: [ContactColumn.Note]
     */
    val note: com.alexstyl.contactstore.Note?

    /**
     * Requires: [ContactColumn.Organization]
     */
    val organization: String?

    /**
     * Requires: [ContactColumn.Organization]
     */
    val jobTitle: String?
    val isStarred: Boolean
    val columns: List<ContactColumn>
}

fun Contact.containsColumn(column: ContactColumn): Boolean {
    return columns.any { it == column }
}

fun Contact.containsLinkedAccountColumns(): Boolean {
    return columns.any { it is LinkedAccountValues }
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
        firstName = if (containsColumn(Names)) firstName else null,
        organization = if (containsColumn(Organization)) organization else null,
        jobTitle = if (containsColumn(Organization)) jobTitle else null,
        lastName = if (containsColumn(Names)) lastName else null,
        isStarred = isStarred,
        imageData = if (containsColumn(Image)) imageData else null,
        phones = if (containsColumn(Phones)) phones.toMutableList() else mutableListOf(),
        mails = if (containsColumn(Mails)) mails.toMutableList() else mutableListOf(),
        events = if (containsColumn(Events)) events.toMutableList() else mutableListOf(),
        postalAddresses = if (containsColumn(PostalAddresses))
            postalAddresses.toMutableList()
        else
            mutableListOf(),
        webAddresses = if (containsColumn(WebAddresses))
            webAddresses.toMutableList()
        else
            mutableListOf(),
        note = if (containsColumn(Note)) note else null,
        columns = columns,
        middleName = if (containsColumn(Names)) middleName else null,
        prefix = if (containsColumn(Names)) prefix else null,
        suffix = if (containsColumn(Names)) suffix else null,
        phoneticNameStyle = if (containsColumn(Names)) phoneticNameStyle else PhoneticNameStyle.UNDEFINED,
        phoneticFirstName = if (containsColumn(Names)) phoneticFirstName else null,
        phoneticLastName = if (containsColumn(Names)) phoneticLastName else null,
        phoneticMiddleName = if (containsColumn(Names)) phoneticMiddleName else null,
        groups = if (containsColumn(GroupMemberships)) groups.toMutableList() else mutableListOf(),
        fullNameStyle = if (containsColumn(Names)) phoneticNameStyle else FullNameStyle.UNDEFINED,
        nickname = if (containsColumn(Nickname)) nickname else null,
        linkedAccountValues = if (containsLinkedAccountColumns()) {
            linkedAccountValues
        } else {
            emptyList()
        },
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
