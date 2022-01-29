package com.alexstyl.contactstore

import android.net.Uri
import com.alexstyl.contactstore.ContactColumn.Events
import com.alexstyl.contactstore.ContactColumn.ImAddresses
import com.alexstyl.contactstore.ContactColumn.Mails
import com.alexstyl.contactstore.ContactColumn.Names
import com.alexstyl.contactstore.ContactColumn.Note
import com.alexstyl.contactstore.ContactColumn.Organization
import com.alexstyl.contactstore.ContactColumn.Phones
import com.alexstyl.contactstore.ContactColumn.PostalAddresses
import com.alexstyl.contactstore.ContactColumn.Relations
import com.alexstyl.contactstore.ContactColumn.SipAddresses
import com.alexstyl.contactstore.ContactColumn.WebAddresses
import com.alexstyl.contactstore.Label.RelationManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test

@ExperimentalCoroutinesApi
internal class AddValuesToExistingContactContactStoreTest : ContactStoreTestBase() {

    @Test
    fun updatesNames(): Unit = runBlocking {
        val contact = buildStoreContact(Names) {
            firstName = "Paolo"
            lastName = "Melendez"
        }

        val updated = contact.mutableCopy {
            prefix = "A."
            firstName = "Paolo"
            middleName = "M."
            lastName = "Melendez"
            suffix = "Z."
        }
        store.execute {
            update(updated)
        }

        assertContactUpdated(updated)
    }

    @Test
    fun addsNewPhones(): Unit = runBlocking {
        val contact = buildStoreContact(Names, Phones) {
            firstName = "Paolo"
            lastName = "Melendez"
        }

        val expected = contact.mutableCopy {
            phones.add(
                LabeledValue(
                    PhoneNumber("555"),
                    Label.PhoneNumberMobile
                )
            )
        }

        store.execute {
            update(expected)
        }

        assertContactUpdatedNoId(expected)
    }

    @Test
    fun addsNewMails(): Unit = runBlocking {
        val contact = buildStoreContact(Names, Mails) {
            firstName = "Paolo"
            lastName = "Melendez"
        }

        val expected = contact.mutableCopy {
            mails.add(
                LabeledValue(
                    MailAddress("555@mail.com"),
                    Label.LocationHome
                )
            )
        }

        store.execute {
            update(expected)
        }

        assertContactUpdatedNoId(expected)
    }

    @Test
    fun addsNewEvents(): Unit = runBlocking {
        val contact = buildStoreContact(Names, Events) {
            firstName = "Paolo"
            lastName = "Melendez"
        }

        val expected = contact.mutableCopy {
            events.add(
                LabeledValue(
                    EventDate(1, 1, 2020),
                    Label.DateBirthday
                )
            )
        }

        store.execute {
            update(expected)
        }

        assertContactUpdatedNoId(expected)
    }

    @Test
    fun addsNewPostalAddress(): Unit = runBlocking {
        val contact = buildStoreContact(Names, PostalAddresses) {
            firstName = "Paolo"
            lastName = "Melendez"
        }

        val expected = contact.mutableCopy {
            postalAddresses.add(
                LabeledValue(
                    PostalAddress("SomeStreet 35"),
                    Label.LocationHome
                )
            )
        }

        store.execute {
            update(expected)
        }

        assertContactUpdatedNoId(expected)
    }

    @Test
    fun addsNewWebAddress(): Unit = runBlocking {
        val contact = buildStoreContact(Names, WebAddresses) {
            firstName = "Paolo"
            lastName = "Melendez"
        }

        val expected = contact.mutableCopy {
            webAddresses.add(
                LabeledValue(
                    WebAddress(Uri.parse("https://web/address")),
                    Label.WebsiteProfile
                )
            )
        }

        store.execute {
            update(expected)
        }

        assertContactUpdatedNoId(expected)
    }

    @Test
    fun updatesOrganization(): Unit = runBlocking {
        val contact = buildStoreContact(Names, Organization) {
            firstName = "Paolo"
            lastName = "Melendez"
        }

        val expected = contact.mutableCopy().apply {
            organization = "Acme"
            jobTitle = "Member"
        }

        store.execute {
            update(expected)
        }

        assertContactUpdatedNoId(expected)
    }

    @Test
    fun updatesNote(): Unit = runBlocking {
        val contact = buildStoreContact(Names, Note) {
            firstName = "Paolo"
            lastName = "Melendez"
        }

        val expected = contact.mutableCopy {
            note = Note("To infinity and beyond!")
        }

        store.execute {
            update(expected)
        }

        assertContactUpdatedNoId(expected)
    }

    @Test
    fun updatesIm(): Unit = runBlocking {
        val contact = buildStoreContact(Names, ImAddresses) {
            firstName = "Paolo"
            lastName = "Melendez"
        }

        val expected = contact.mutableCopy {
            imAddresses.add(
                LabeledValue(
                    ImAddress("address", protocol = "protocol"),
                    Label.LocationHome
                )
            )
        }

        store.execute {
            update(expected)
        }

        assertContactUpdatedNoId(expected)
    }

    @Test
    fun updatesRelation(): Unit = runBlocking {
        val contact = buildStoreContact(Names, Relations) {
            firstName = "Paolo"
            lastName = "Melendez"
        }

        val expected = contact.mutableCopy {
            relations.add(
                LabeledValue(
                    Relation(name = "Maria"),
                    RelationManager
                )
            )
        }
        store.execute {
            update(expected)
        }

        assertContactUpdatedNoId(expected)
    }

    @Test
    fun updatesSipAddresses(): Unit = runBlocking {
        val contact = buildStoreContact(Names, SipAddresses) {
            firstName = "Paolo"
            lastName = "Melendez"
        }

        val expected = contact.mutableCopy {
            sipAddresses.add(
                LabeledValue(
                    SipAddress("123"),
                    Label.LocationHome
                )
            )
        }
        store.execute {
            update(expected)
        }

        assertContactUpdatedNoId(expected)
    }

}
