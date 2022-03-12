package com.alexstyl.contactstore.test

import android.net.Uri
import com.alexstyl.contactstore.*

internal object SnapshotFixtures {
    val KIM_CLAY = StoredContact(
        contactId = 1,
        firstName = "Kim",
        lastName = "Clay",
        isStarred = false
    )
    val PAOLO_MELENDEZ = StoredContact(
        contactId = 0L,
        lookupKey = ContactFixtures.PAOLO_MELENDEZ.lookupKey,
        isStarred = false,
        firstName = ContactFixtures.PAOLO_MELENDEZ.firstName,
        lastName = ContactFixtures.PAOLO_MELENDEZ.lastName,
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
        groups = listOf(GroupMembership(groupId = 10))
    )
}