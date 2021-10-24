package com.alexstyl.contactstore

import android.provider.ContactsContract
import com.alexstyl.contactstore.ContactColumn.Events
import com.alexstyl.contactstore.ContactColumn.GroupMemberships
import com.alexstyl.contactstore.ContactColumn.Image
import com.alexstyl.contactstore.ContactColumn.Mails
import com.alexstyl.contactstore.ContactColumn.Names
import com.alexstyl.contactstore.ContactColumn.Nickname
import com.alexstyl.contactstore.ContactColumn.Note
import com.alexstyl.contactstore.ContactColumn.Organization
import com.alexstyl.contactstore.ContactColumn.Phones
import com.alexstyl.contactstore.ContactColumn.PostalAddresses
import com.alexstyl.contactstore.ContactColumn.WebAddresses

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
    note: com.alexstyl.contactstore.Note? = null,
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
    override val prefix by readField(Names, prefix)
    override val firstName by readField(Names, firstName)
    override val middleName by readField(Names, middleName)
    override val lastName by readField(Names, lastName)
    override val suffix by readField(Names, suffix)
    override val phoneticFirstName by readField(Names, phoneticFirstName)
    override val phoneticMiddleName by readField(Names, phoneticMiddleName)
    override val phoneticLastName by readField(Names, phoneticLastName)
    override val nickname by readField(Nickname, nickname)
    override val fullNameStyle by readField(Names, fullNameStyle)
    override val phoneticNameStyle by readField(Names, phoneticNameStyle)
    override val imageData by readField(Image, imageData)
    override val phones by readField(Phones, phones)
    override val mails by readField(Mails, mails)
    override val events by readField(Events, events)
    override val postalAddresses by readField(PostalAddresses, postalAddresses)
    override val note by readField(Note, note)
    override val webAddresses by readField(WebAddresses, webAddresses)
    override val organization by readField(Organization, organization)
    override val jobTitle by readField(Organization, jobTitle)

    override val groups: List<GroupMembership> by readField(GroupMemberships, groups)

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
