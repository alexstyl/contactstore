package com.alexstyl.contactstore.test

import android.provider.ContactsContract
import com.alexstyl.contactstore.Contact
import com.alexstyl.contactstore.ContactColumn
import com.alexstyl.contactstore.CustomDataItem
import com.alexstyl.contactstore.EventDate
import com.alexstyl.contactstore.GroupMembership
import com.alexstyl.contactstore.ImAddress
import com.alexstyl.contactstore.ImageData
import com.alexstyl.contactstore.LabeledValue
import com.alexstyl.contactstore.LookupKey
import com.alexstyl.contactstore.MailAddress
import com.alexstyl.contactstore.Note
import com.alexstyl.contactstore.PhoneNumber
import com.alexstyl.contactstore.PostalAddress
import com.alexstyl.contactstore.Relation
import com.alexstyl.contactstore.SipAddress
import com.alexstyl.contactstore.WebAddress

public data class StoredContact(
    override val contactId: Long,
    override val isStarred: Boolean = false,
    override val prefix: String = "",
    override val firstName: String = "",
    override val middleName: String = "",
    override val lastName: String = "",
    override val suffix: String = "",
    override val phoneticFirstName: String = "",
    override val phoneticMiddleName: String = "",
    override val phoneticLastName: String = "",
    override val imageData: ImageData? = null,
    override val organization: String = "",
    override val jobTitle: String = "",
    override val webAddresses: List<LabeledValue<WebAddress>> = emptyList(),
    override val phones: List<LabeledValue<PhoneNumber>> = emptyList(),
    override val mails: List<LabeledValue<MailAddress>> = emptyList(),
    override val events: List<LabeledValue<EventDate>> = emptyList(),
    override val postalAddresses: List<LabeledValue<PostalAddress>> = emptyList(),
    override val note: Note? = null,
    override val nickname: String = "",
    override val fullNameStyle: Int = ContactsContract.FullNameStyle.UNDEFINED,
    override val phoneticNameStyle: Int = ContactsContract.PhoneticNameStyle.UNDEFINED,
    override val groups: List<GroupMembership> = emptyList(),
    override val displayName: String = "",
    override val lookupKey: LookupKey? = null,
    override val sipAddresses: List<LabeledValue<SipAddress>> = emptyList(),
    override val customDataItems: List<CustomDataItem> = emptyList(),
    override val imAddresses: List<LabeledValue<ImAddress>> = emptyList(),
    override val relations: List<LabeledValue<Relation>> = emptyList(),
    override val columns: List<ContactColumn> = emptyList()
) : Contact
