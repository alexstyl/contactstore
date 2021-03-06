package com.alexstyl.contactstore.test

import android.net.Uri
import com.alexstyl.contactstore.EventDate
import com.alexstyl.contactstore.GroupMembership
import com.alexstyl.contactstore.ImageData
import com.alexstyl.contactstore.Label
import com.alexstyl.contactstore.LabeledValue
import com.alexstyl.contactstore.LookupKey
import com.alexstyl.contactstore.MailAddress
import com.alexstyl.contactstore.Note
import com.alexstyl.contactstore.PartialContact
import com.alexstyl.contactstore.PhoneNumber
import com.alexstyl.contactstore.PostalAddress
import com.alexstyl.contactstore.WebAddress
import com.alexstyl.contactstore.allContactColumns

internal object ContactFixtures {
    val PAOLO_MELENDEZ = PartialContact(
        contactId = 0L,
        lookupKey = LookupKey("test-lookup-paolo"),
        displayName = "Prefix Paolo Mid Melendez, Suffix",
        columns = allContactColumns(),
        isStarred = false,
        firstName = "Paolo",
        lastName = "Melendez",
        phones = listOf(
            LabeledValue(PhoneNumber("555-15"), Label.PhoneNumberMobile)
        ),
        mails = listOf(
            LabeledValue(MailAddress("hi@mail.com"), Label.LocationHome)
        ),
        postalAddresses = listOf(
            LabeledValue(PostalAddress("SomeStreet 55"), Label.LocationHome)
        ),
        imageData = ImageData("imagedata".toByteArray()),
        organization = "Organization",
        jobTitle = "Job Title",
        webAddresses = listOf(
            LabeledValue(WebAddress(Uri.parse("www.web.com")), Label.WebsiteHomePage)
        ),
        events = listOf(
            LabeledValue(EventDate(1, 1, 2021), Label.DateBirthday)
        ),
        note = Note("note"),
        prefix = "Prefix",
        middleName = "Mid",
        suffix = "Suffix",
        nickname = "Nickname",
        groups = listOf(GroupMembership(groupId = 10)),
    )
}
