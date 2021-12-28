package com.alexstyl.contactstore

import com.alexstyl.contactstore.test.StoredContact

object SnapshotFixtures {
    val KIM_CLAY = StoredContact(
        contactId = 1,
        firstName = "Kim",
        lastName = "Clay",
        isStarred = false
    )
    val PAOLO_MELENDEZ = StoredContact(
        contactId = 0L,
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
            LabeledValue(WebAddress("www.web.com"), Label.WebsiteHomePage)
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

object ContactFixtures {
    val PAOLO_MELENDEZ = PartialContact(
        contactId = 0L,
        displayName = "Prefix Paolo Mid Melendez, Suffix",
        columns = standardColumns(),
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
            LabeledValue(WebAddress("www.web.com"), Label.WebsiteHomePage)
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
        lookupKey = null,
    )
}
