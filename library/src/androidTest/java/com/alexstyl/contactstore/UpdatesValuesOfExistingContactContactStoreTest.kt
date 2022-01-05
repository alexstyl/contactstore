package com.alexstyl.contactstore

import com.alexstyl.contactstore.ContactColumn.*
import com.alexstyl.contactstore.Label.LocationHome
import kotlinx.coroutines.runBlocking
import org.junit.Test

internal class UpdatesValuesOfExistingContactContactStoreTest : ContactStoreTestBase() {

    @Test
    fun updatesExistingPhone(): Unit = runBlocking {
        val contactToUpdate = buildStoreContact(Names, Phones) {
            firstName = "Paolo"
            lastName = "Melendez"
            phones.add(
                LabeledValue(PhoneNumber("555"), LocationHome)
            )
        }

        val editedContact = contactToUpdate.mutableCopy().apply {
            val id = phones.first().requireId()
            phones.replaceId(
                id,
                LabeledValue(
                    PhoneNumber("666"),
                    LocationHome,
                    id
                )
            )
        }
        assertContactUpdated(editedContact)
    }

    @Test
    fun updatesExistingMails(): Unit = runBlocking {
        val contactToUpdate = buildStoreContact(Names, Mails) {
            firstName = "Paolo"
            lastName = "Melendez"
            mails.add(
                LabeledValue(MailAddress("mail@mail.com"), LocationHome)
            )
        }

        val editedContact = contactToUpdate.mutableCopy().apply {
            val id = mails.first().requireId()
            mails.replaceId(
                id,
                LabeledValue(
                    MailAddress("mail@mail.eu"),
                    LocationHome,
                    id
                )
            )
        }

        assertContactUpdated(editedContact)
    }

    @Test
    fun updatesExistingPostals(): Unit = runBlocking {
        val contactToUpdate = buildStoreContact(Names, PostalAddresses) {
            firstName = "Paolo"
            lastName = "Melendez"
            postalAddresses.add(
                LabeledValue(PostalAddress("Somewhere Street 53"), LocationHome)
            )
        }

        val editedContact = contactToUpdate.mutableCopy().apply {
            val id = postalAddresses.first().requireId()
            postalAddresses.replaceId(
                id,
                LabeledValue(
                    PostalAddress("Somewhere Street 35"),
                    LocationHome,
                    id
                )
            )
        }

        assertContactUpdated(editedContact)
    }


    @Test
    fun updatesExistingEvents(): Unit = runBlocking {
        val contactToUpdate = buildStoreContact(Names, Events) {
            firstName = "Paolo"
            lastName = "Melendez"
            events.add(
                LabeledValue(EventDate(1, 1, 2020), Label.DateBirthday)
            )
        }

        val editedContact = contactToUpdate.mutableCopy().apply {
            val id = events.first().requireId()
            events.replaceId(
                id,
                LabeledValue(
                    EventDate(
                        1,
                        1,
                        2021
                    ), Label.DateBirthday, id
                )
            )
        }

        assertContactUpdated(editedContact)
    }

    @Test
    fun updatesExistingWebsites(): Unit = runBlocking {
        val contactToUpdate = buildStoreContact(Names, WebAddresses) {
            firstName = "Paolo"
            lastName = "Melendez"
            webAddresses.add(
                LabeledValue(WebAddress("https://web/address"), Label.WebsiteProfile)
            )
        }

        val editedContact = contactToUpdate.mutableCopy().apply {
            val id = webAddresses.first().requireId()
            webAddresses.replaceId(
                id,
                LabeledValue(
                    WebAddress("https://web/address.com"),
                    Label.WebsiteProfile,
                    id
                )
            )
        }

        assertContactUpdated(editedContact)
    }
}

private fun <T : Any> MutableList<LabeledValue<T>>.replaceId(
    id: Long,
    withValue: LabeledValue<T>
) {
    val updatedPhones = map {
        if (it.requireId() == id) {
            withValue
        } else {
            it
        }
    }
    clear()
    addAll(updatedPhones)
}

