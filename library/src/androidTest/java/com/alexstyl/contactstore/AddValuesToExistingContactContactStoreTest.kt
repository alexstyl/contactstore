package com.alexstyl.contactstore

import com.alexstyl.contactstore.ContactColumn.EVENTS
import com.alexstyl.contactstore.ContactColumn.MAILS
import com.alexstyl.contactstore.ContactColumn.NAMES
import com.alexstyl.contactstore.ContactColumn.NOTE
import com.alexstyl.contactstore.ContactColumn.ORGANIZATION
import com.alexstyl.contactstore.ContactColumn.PHONES
import com.alexstyl.contactstore.ContactColumn.POSTAL_ADDRESSES
import com.alexstyl.contactstore.ContactColumn.WEB_ADDRESSES
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test

@ExperimentalCoroutinesApi
class AddValuesToExistingContactContactStoreTest : ContactStoreTestBase() {

    @Test
    fun updatesNames(): Unit = runBlocking {
        val contact = buildStoreContact(NAMES) {
            firstName = "Paolo"
            lastName = "Melendez"
        }

        val updated = contact.mutableCopy().apply {
            prefix = "A."
            firstName = "Paolo"
            middleName = "M."
            lastName = "Melendez"
            suffix = "Z."
        }
        store.execute(SaveRequest().apply {
            update(updated)
        })

        assertContactUpdated(updated)
    }

    @Test
    fun addsNewPhones(): Unit = runBlocking {
        val contact = buildStoreContact(NAMES, PHONES) {
            firstName = "Paolo"
            lastName = "Melendez"
        }

        val expected = contact.mutableCopy().apply {
            phones.add(
                LabeledValue(
                    PhoneNumber("555"),
                    Label.PhoneNumberMobile
                )
            )
        }

        store.execute(SaveRequest().apply {
            update(expected)
        })

        assertContactUpdatedNoId(expected)
    }

    @Test
    fun addsNewMails(): Unit = runBlocking {
        val contact = buildStoreContact(NAMES, MAILS) {
            firstName = "Paolo"
            lastName = "Melendez"
        }

        val expected = contact.mutableCopy().apply {
            mails.add(
                LabeledValue(
                    MailAddress("555@mail.com"),
                    Label.LocationHome
                )
            )
        }

        store.execute(SaveRequest().apply {
            update(expected)
        })

        assertContactUpdatedNoId(expected)
    }

    @Test
    fun addsNewEvents(): Unit = runBlocking {
        val contact = buildStoreContact(NAMES, EVENTS) {
            firstName = "Paolo"
            lastName = "Melendez"
        }

        val expected = contact.mutableCopy().apply {
            events.add(
                LabeledValue(
                    EventDate(1, 1, 2020),
                    Label.DateBirthday
                )
            )
        }

        store.execute(SaveRequest().apply {
            update(expected)
        })

        assertContactUpdatedNoId(expected)
    }

    @Test
    fun addsNewPostalAddress(): Unit = runBlocking {
        val contact = buildStoreContact(NAMES, POSTAL_ADDRESSES) {
            firstName = "Paolo"
            lastName = "Melendez"
        }

        val expected = contact.mutableCopy().apply {
            postalAddresses.add(
                LabeledValue(
                    PostalAddress("SomeStreet 35"),
                    Label.LocationHome
                )
            )
        }

        store.execute(SaveRequest().apply {
            update(expected)
        })

        assertContactUpdatedNoId(expected)
    }

    @Test
    fun addsNewWebAddress(): Unit = runBlocking {
        val contact = buildStoreContact(NAMES, WEB_ADDRESSES) {
            firstName = "Paolo"
            lastName = "Melendez"
        }

        val expected = contact.mutableCopy().apply {
            webAddresses.add(
                LabeledValue(
                    WebAddress("https://web/address"),
                    Label.WebsiteProfile
                )
            )
        }

        store.execute(SaveRequest().apply {
            update(expected)
        })

        assertContactUpdatedNoId(expected)
    }

    @Test
    fun updatesOrganization(): Unit = runBlocking {
        val contact = buildStoreContact(NAMES, ORGANIZATION) {
            firstName = "Paolo"
            lastName = "Melendez"
        }

        val expected = contact.mutableCopy().apply {
            organization = "Acme"
            jobTitle = "Member"
        }

        store.execute(SaveRequest().apply {
            update(expected)
        })

        assertContactUpdatedNoId(expected)
    }

    @Test
    fun updatesNote(): Unit = runBlocking {
        val contact = buildStoreContact(NAMES, NOTE) {
            firstName = "Paolo"
            lastName = "Melendez"
        }

        val expected = contact.mutableCopy().apply {
            note = Note("To infinity and beyond!")
        }

        store.execute(SaveRequest().apply {
            update(expected)
        })

        assertContactUpdatedNoId(expected)
    }
}
