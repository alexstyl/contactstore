package com.alexstyl.contactstore.test

import android.provider.ContactsContract
import com.alexstyl.contactstore.EventDate
import com.alexstyl.contactstore.GroupMembership
import com.alexstyl.contactstore.ImageData
import com.alexstyl.contactstore.LabeledValue
import com.alexstyl.contactstore.MailAddress
import com.alexstyl.contactstore.Note
import com.alexstyl.contactstore.PhoneNumber
import com.alexstyl.contactstore.PostalAddress
import com.alexstyl.contactstore.WebAddress

public data class StoredContact(
    val contactId: Long,
    val isStarred: Boolean = false,
    val prefix: String? = null,
    val firstName: String? = null,
    val middleName: String? = null,
    val lastName: String? = null,
    val suffix: String? = null,
    val phoneticFirstName: String? = null,
    val phoneticMiddleName: String? = null,
    val phoneticLastName: String? = null,
    val imageData: ImageData? = null,
    val organization: String? = null,
    val jobTitle: String? = null,
    val webAddresses: List<LabeledValue<WebAddress>> = emptyList(),
    val phones: List<LabeledValue<PhoneNumber>> = emptyList(),
    val mails: List<LabeledValue<MailAddress>> = emptyList(),
    val events: List<LabeledValue<EventDate>> = emptyList(),
    val postalAddresses: List<LabeledValue<PostalAddress>> = emptyList(),
    val note: Note? = null,
    val nickname: String? = null,
    val fullNameStyle: Int = ContactsContract.FullNameStyle.UNDEFINED,
    val phoneticNameStyle: Int = ContactsContract.PhoneticNameStyle.UNDEFINED,
    val groups: List<GroupMembership> = emptyList()
)