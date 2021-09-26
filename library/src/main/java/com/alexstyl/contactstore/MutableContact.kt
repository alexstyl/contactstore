package com.alexstyl.contactstore

import android.provider.ContactsContract
import com.alexstyl.contactstore.ContactColumn.EVENTS
import com.alexstyl.contactstore.ContactColumn.GROUP_MEMBERSHIPS
import com.alexstyl.contactstore.ContactColumn.IMAGE
import com.alexstyl.contactstore.ContactColumn.MAILS
import com.alexstyl.contactstore.ContactColumn.NAMES
import com.alexstyl.contactstore.ContactColumn.NOTE
import com.alexstyl.contactstore.ContactColumn.ORGANIZATION
import com.alexstyl.contactstore.ContactColumn.PHONES
import com.alexstyl.contactstore.ContactColumn.POSTAL_ADDRESSES
import com.alexstyl.contactstore.ContactColumn.WEB_ADDRESSES
import com.alexstyl.contactstore.ContactColumn.values

class MutableContact internal constructor(
    override var contactId: Long = -1L,
    imageData: ImageData?,
    phones: MutableList<LabeledValue<PhoneNumber>>,
    mails: MutableList<LabeledValue<MailAddress>>,
    events: MutableList<LabeledValue<EventDate>>,
    postalAddresses: MutableList<LabeledValue<PostalAddress>>,
    webAddresses: MutableList<LabeledValue<WebAddress>>,
    note: Note?,
    override var isStarred: Boolean,
    firstName: String?,
    lastName: String?,
    middleName: String?,
    prefix: String?,
    suffix: String?,
    phoneticFirstName: String?,
    phoneticMiddleName: String?,
    phoneticLastName: String?,
    fullNameStyle: Int,
    phoneticNameStyle: Int,
    nickname: String?,
    organization: String?,
    jobTitle: String?,
    groups: MutableList<GroupMembership>,
    override val columns: List<ContactColumn>,
) : Contact {

    override var imageData: ImageData? by readWriteField(IMAGE, imageData)
    override val phones: MutableList<LabeledValue<PhoneNumber>> by readField(PHONES, phones)
    override val mails: MutableList<LabeledValue<MailAddress>> by readField(MAILS, mails)
    override val events: MutableList<LabeledValue<EventDate>> by readField(EVENTS, events)
    override val postalAddresses: MutableList<LabeledValue<PostalAddress>>
            by readField(POSTAL_ADDRESSES, postalAddresses)
    override val webAddresses: MutableList<LabeledValue<WebAddress>>
            by readField(WEB_ADDRESSES, webAddresses)
    override var note: Note? by readWriteField(NOTE, note)

    override val groups: MutableList<GroupMembership> by readField(GROUP_MEMBERSHIPS, groups)

    override var organization: String? by readWriteField(ORGANIZATION, organization)
    override var jobTitle: String? by readWriteField(ORGANIZATION, jobTitle)
    override var firstName: String? by readWriteField(NAMES, firstName)
    override var lastName: String? by readWriteField(NAMES, lastName)
    override var middleName: String? by readWriteField(NAMES, middleName)
    override var prefix: String? by readWriteField(NAMES, prefix)
    override var suffix: String? by readWriteField(NAMES, suffix)
    override var phoneticLastName: String? by readWriteField(NAMES, phoneticLastName)
    override var phoneticFirstName: String? by readWriteField(NAMES, phoneticFirstName)
    override var phoneticMiddleName: String? by readWriteField(NAMES, phoneticMiddleName)
    override var fullNameStyle: Int by readWriteField(NAMES, fullNameStyle)
    override var phoneticNameStyle: Int by readWriteField(NAMES, phoneticNameStyle)
    override var nickname: String? by readWriteField(NAMES, nickname)

    constructor() : this(
        contactId = -1L,
        imageData = null,
        phones = mutableListOf(),
        mails = mutableListOf(),
        events = mutableListOf(),
        postalAddresses = mutableListOf(),
        webAddresses = mutableListOf(),
        note = null,
        isStarred = false,
        firstName = null,
        lastName = null,
        middleName = null,
        prefix = null,
        suffix = null,
        phoneticFirstName = null,
        phoneticMiddleName = null,
        phoneticLastName = null,
        fullNameStyle = ContactsContract.FullNameStyle.UNDEFINED,
        phoneticNameStyle = ContactsContract.PhoneticNameStyle.UNDEFINED,
        nickname = null,
        organization = null,
        jobTitle = null,
        groups = mutableListOf(),
        columns = values().toList() // allow editing of all columns for new contacts
    )

    override val displayName: String
        get() = buildString {
            appendWord(buildStringFromNames())

            if (isEmpty()) {
                phoneticFirstName?.let { append(it) }
                phoneticMiddleName?.let { appendWord(it) }
                phoneticLastName?.let { appendWord(it) }
            }
            if (isEmpty()) {
                append(nickname.orEmpty())
            }
            if (isEmpty()) {
                append(organization.orEmpty())
            }
            if (isEmpty()) {
                phones.firstOrNull()?.let { append(it.value.raw) }
            }
            if (isEmpty()) {
                mails.firstOrNull()?.let { append(it.value.raw) }
            }
        }

    override fun containsColumn(column: ContactColumn): Boolean {
        return columns.contains(column)
    }

    override fun equals(other: Any?): Boolean {
        return equalContacts(other as Contact?)
    }

    override fun hashCode(): Int {
        return contactHashCode()
    }

    override fun toString(): String {
        return toFullString()
    }
}