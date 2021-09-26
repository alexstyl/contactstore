package com.alexstyl.contactstore

import android.provider.ContactsContract
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

class PartialContact constructor(
    override val contactId: Long,
    override val columns: List<ContactColumn>,
    override val isStarred: Boolean,
    override val displayName: String?,
    firstName: String? = null,
    lastName: String? = null,
    imageData: ImageData? = null,
    organization: String? = null,
    jobTitle: String? = null,
    webAddresses: List<LabeledValue<WebAddress>> = emptyList(),
    phones: List<LabeledValue<PhoneNumber>> = emptyList(),
    mails: List<LabeledValue<MailAddress>> = emptyList(),
    events: List<LabeledValue<EventDate>> = emptyList(),
    postalAddresses: List<LabeledValue<PostalAddress>> = emptyList(),
    note: Note? = null,
    prefix: String? = null,
    middleName: String? = null,
    suffix: String? = null,
    phoneticFirstName: String? = null,
    phoneticMiddleName: String? = null,
    phoneticLastName: String? = null,
    nickname: String? = null,
    fullNameStyle: Int = ContactsContract.FullNameStyle.UNDEFINED,
    phoneticNameStyle: Int = ContactsContract.PhoneticNameStyle.UNDEFINED,
    groups : List<GroupMembership> = emptyList()
) : Contact {
    override val prefix by readField(NAMES, prefix)
    override val firstName by readField(NAMES, firstName)
    override val middleName by readField(NAMES, middleName)
    override val lastName by readField(NAMES, lastName)
    override val suffix by readField(NAMES, suffix)
    override val phoneticFirstName by readField(NAMES, phoneticFirstName)
    override val phoneticMiddleName by readField(NAMES, phoneticMiddleName)
    override val phoneticLastName by readField(NAMES, phoneticLastName)
    override val nickname by readField(NICKNAME, nickname)
    override val fullNameStyle by readField(NAMES, fullNameStyle)
    override val phoneticNameStyle by readField(NAMES, phoneticNameStyle)
    override val imageData by readField(IMAGE, imageData)
    override val phones by readField(PHONES, phones)
    override val mails by readField(MAILS, mails)
    override val events by readField(EVENTS, events)
    override val postalAddresses by readField(POSTAL_ADDRESSES, postalAddresses)
    override val note by readField(NOTE, note)
    override val webAddresses by readField(WEB_ADDRESSES, webAddresses)
    override val organization by readField(ORGANIZATION, organization)
    override val jobTitle by readField(ORGANIZATION, jobTitle)

    override val groups: List<GroupMembership> by readField(GROUP_MEMBERSHIPS, groups)

    override fun containsColumn(column: ContactColumn): Boolean {
        return columns.contains(column)
    }

    override fun equals(other: Any?): Boolean {
        return equalContacts(other as? Contact)
    }

    override fun hashCode(): Int {
        return contactHashCode()
    }

    override fun toString(): String {
        return toFullString()
    }
}
