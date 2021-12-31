package com.alexstyl.contactstore

import android.provider.ContactsContract
import com.alexstyl.contactstore.ContactColumn.Events
import com.alexstyl.contactstore.ContactColumn.GroupMemberships
import com.alexstyl.contactstore.ContactColumn.ImAddresses
import com.alexstyl.contactstore.ContactColumn.Image
import com.alexstyl.contactstore.ContactColumn.Mails
import com.alexstyl.contactstore.ContactColumn.Names
import com.alexstyl.contactstore.ContactColumn.Nickname
import com.alexstyl.contactstore.ContactColumn.Note
import com.alexstyl.contactstore.ContactColumn.Organization
import com.alexstyl.contactstore.ContactColumn.Phones
import com.alexstyl.contactstore.ContactColumn.PostalAddresses
import com.alexstyl.contactstore.ContactColumn.Relations
import com.alexstyl.contactstore.ContactColumn.SipAddresses
import com.alexstyl.contactstore.ContactColumn.WebAddresses

class PartialContact constructor(
    override val contactId: Long,
    override val lookupKey: LookupKey?,
    override val columns: List<ContactColumn>,
    override val isStarred: Boolean,
    override val displayName: String?,
    firstName: String? = null,
    lastName: String? = null,
    imageData: ImageData? = null,
    organization: String? = null,
    jobTitle: String? = null,
    webAddresses: List<LabeledValue<WebAddress>> = emptyList(),
    sipAddresses: List<LabeledValue<SipAddress>> = emptyList(),
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
    groups: List<GroupMembership> = emptyList(),
    imAddresses: List<LabeledValue<ImAddress>> = emptyList(),
    linkedAccountValues: List<LinkedAccountValue> = emptyList(),
    relations: List<LabeledValue<Relation>> = emptyList()
) : Contact {
    override val prefix by requireColumn(Names, prefix)
    override val firstName by requireColumn(Names, firstName)
    override val middleName by requireColumn(Names, middleName)
    override val lastName by requireColumn(Names, lastName)
    override val imAddresses by requireColumn(ImAddresses, imAddresses)
    override val suffix by requireColumn(Names, suffix)
    override val phoneticFirstName by requireColumn(Names, phoneticFirstName)
    override val phoneticMiddleName by requireColumn(Names, phoneticMiddleName)
    override val phoneticLastName by requireColumn(Names, phoneticLastName)
    override val nickname by requireColumn(Nickname, nickname)
    override val fullNameStyle by requireColumn(Names, fullNameStyle)
    override val phoneticNameStyle by requireColumn(Names, phoneticNameStyle)
    override val imageData by requireColumn(Image, imageData)
    override val phones by requireColumn(Phones, phones)
    override val mails by requireColumn(Mails, mails)
    override val events by requireColumn(Events, events)
    override val postalAddresses by requireColumn(PostalAddresses, postalAddresses)
    override val note by requireColumn(Note, note)
    override val webAddresses by requireColumn(WebAddresses, webAddresses)
    override val sipAddresses by requireColumn(SipAddresses, sipAddresses)
    override val organization by requireColumn(Organization, organization)
    override val jobTitle by requireColumn(Organization, jobTitle)
    override val linkedAccountValues by requireAnyLinkedAccountColumn(linkedAccountValues)
    override val groups: List<GroupMembership> by requireColumn(GroupMemberships, groups)
    override val relations: List<LabeledValue<Relation>> by requireColumn(Relations, relations)

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
