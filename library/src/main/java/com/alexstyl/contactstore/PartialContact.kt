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

public class PartialContact constructor(
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
    override val prefix: String? by requireColumn(Names, prefix)
    override val firstName: String? by requireColumn(Names, firstName)
    override val middleName: String? by requireColumn(Names, middleName)
    override val lastName: String? by requireColumn(Names, lastName)
    override val imAddresses: List<LabeledValue<ImAddress>> by requireColumn(ImAddresses, imAddresses)
    override val suffix: String? by requireColumn(Names, suffix)
    override val phoneticFirstName: String? by requireColumn(Names, phoneticFirstName)
    override val phoneticMiddleName: String? by requireColumn(Names, phoneticMiddleName)
    override val phoneticLastName: String? by requireColumn(Names, phoneticLastName)
    override val nickname: String? by requireColumn(Nickname, nickname)
    override val fullNameStyle: Int by requireColumn(Names, fullNameStyle)
    override val phoneticNameStyle: Int by requireColumn(Names, phoneticNameStyle)
    override val imageData: ImageData? by requireColumn(Image, imageData)
    override val phones: List<LabeledValue<PhoneNumber>> by requireColumn(Phones, phones)
    override val mails: List<LabeledValue<MailAddress>> by requireColumn(Mails, mails)
    override val events: List<LabeledValue<EventDate>> by requireColumn(Events, events)
    override val postalAddresses: List<LabeledValue<PostalAddress>> by requireColumn(PostalAddresses, postalAddresses)
    override val note: com.alexstyl.contactstore.Note? by requireColumn(Note, note)
    override val webAddresses: List<LabeledValue<WebAddress>> by requireColumn(WebAddresses, webAddresses)
    override val sipAddresses: List<LabeledValue<SipAddress>> by requireColumn(SipAddresses, sipAddresses)
    override val organization: String? by requireColumn(Organization, organization)
    override val jobTitle: String? by requireColumn(Organization, jobTitle)
    override val linkedAccountValues: List<LinkedAccountValue> by requireAnyLinkedAccountColumn(linkedAccountValues)
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
