package com.alexstyl.contactstore

import com.alexstyl.contactstore.ContactColumn.Events
import com.alexstyl.contactstore.ContactColumn.ImAddresses
import com.alexstyl.contactstore.ContactColumn.Mails
import com.alexstyl.contactstore.ContactColumn.Names
import com.alexstyl.contactstore.ContactColumn.Note
import com.alexstyl.contactstore.ContactColumn.Organization
import com.alexstyl.contactstore.ContactColumn.Phones
import com.alexstyl.contactstore.ContactColumn.PostalAddresses
import com.alexstyl.contactstore.ContactColumn.WebAddresses
import com.alexstyl.contactstore.Label.DateBirthday
import com.alexstyl.contactstore.Label.LocationHome
import com.alexstyl.contactstore.Label.PhoneNumberMobile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test

@ExperimentalCoroutinesApi
class ContactStoreInsertContactTest : ContactStoreTestBase() {

    @Test
    fun insertsContactWithNames(): Unit = runBlocking {
        store.execute {
            insert(
                MutableContact().apply {
                    prefix = "A."
                    firstName = "Paolo"
                    middleName = "M."
                    lastName = "Melendez"
                    suffix = "Z."
                }
            )
        }

        val actual = store.fetchContacts(columnsToFetch = listOf(Names)).first()
        val expected = contact(
            displayName = "A. Paolo M. Melendez, Z.",
            prefix = "A.",
            firstName = "Paolo",
            middleName = "M.",
            lastName = "Melendez",
            suffix = "Z.",
            columns = listOf(Names)
        )

        assertOnlyContact(actual = actual, expected = expected)
    }

    @Test
    fun insertsContactWithPhones(): Unit = runBlocking {
        store.execute {
            insert(
                MutableContact().apply {
                    phones.add(
                        LabeledValue(
                            value = PhoneNumber("555"),
                            label = PhoneNumberMobile,
                            id = 0
                        )
                    )
                }
            )
        }

        val actual = store.fetchContacts(columnsToFetch = listOf(Phones)).first()
        val expected = contact(
            displayName = "555",
            columns = listOf(Phones),
            phones = listOf(
                LabeledValue(
                    PhoneNumber("555"),
                    PhoneNumberMobile,
                    id = 0
                )
            )
        )

        assertOnlyContact(actual = actual, expected = expected)
    }

    @Test
    fun insertsContactWithMails(): Unit = runBlocking {
        store.execute {
            insert(
                MutableContact().apply {
                    mails.add(
                        LabeledValue(
                            value = MailAddress("555@mail.com"),
                            label = LocationHome,
                            id = 0
                        )
                    )
                }
            )
        }

        val actual = store.fetchContacts(columnsToFetch = listOf(Mails)).first()
        val expected = contact(
            displayName = "555@mail.com",
            columns = listOf(Mails),
            mails = listOf(
                LabeledValue(
                    value = MailAddress("555@mail.com"),
                    label = LocationHome,
                    id = 0
                )
            )
        )

        assertOnlyContact(actual = actual, expected = expected)
    }

    @Test
    fun insertsContactWithEvents(): Unit = runBlocking {
        store.execute {
            insert(
                MutableContact().apply {
                    events.add(
                        LabeledValue(
                            EventDate(
                                1,
                                1,
                                2021
                            ), DateBirthday, id = 0
                        )
                    )
                }
            )
        }

        val actual = store.fetchContacts(columnsToFetch = listOf(Events)).first()
        val expected = contact(
            columns = listOf(Events),
            events = listOf(
                LabeledValue(
                    EventDate(1, 1, 2021), DateBirthday,
                    id = 0
                )
            )
        )

        assertOnlyContact(actual = actual, expected = expected)
    }

    @Test
    fun insertsContactWithPostalAddress(): Unit = runBlocking {
        store.execute {
            insert(
                MutableContact().apply {
                    postalAddresses.add(
                        LabeledValue(
                            PostalAddress(
                                street = "Somestreet",
                                poBox = "12345",
                                neighborhood = "Hood",
                                city = "City",
                                region = "",
                                postCode = "",
                                country = ""
                            ), LocationHome,
                            id = 0
                        )
                    )
                }
            )
        }

        val actual = store.fetchContacts(columnsToFetch = listOf(PostalAddresses)).first()
        val expected = contact(
            columns = listOf(PostalAddresses),
            postalAddresses = listOf(
                LabeledValue(
                    PostalAddress(
                        street = "Somestreet",
                        poBox = "12345",
                        neighborhood = "Hood",
                        city = "City",
                        region = "",
                        postCode = "",
                        country = ""
                    ), LocationHome,
                    id = 0
                )
            )
        )

        assertOnlyContact(actual = actual, expected = expected)
    }

    @Test
    fun insertsContactWithNote(): Unit = runBlocking {
        store.execute {
            insert(
                MutableContact().apply {
                    note = Note("Cool guy")
                }
            )
        }

        val actual = store.fetchContacts(columnsToFetch = listOf(Note)).first()
        val expected = contact(
            columns = listOf(Note),
            note = Note("Cool guy")
        )

        assertOnlyContact(actual = actual, expected = expected)
    }

    @Test
    fun insertsContactWithWebAddresses(): Unit = runBlocking {
        store.execute {
            insert(
                MutableContact().apply {
                    webAddresses.add(
                        LabeledValue(
                            value = WebAddress("https://acme.corp"),
                            label = Label.WebsiteHomePage,
                            id = 0
                        )
                    )
                }
            )
        }

        val actual = store.fetchContacts(columnsToFetch = listOf(WebAddresses)).first()
        val expected = contact(
            columns = listOf(WebAddresses),
            webAddresses = listOf(
                LabeledValue(
                    WebAddress("https://acme.corp"),
                    Label.WebsiteHomePage,
                    id = 0
                )
            )
        )

        assertOnlyContact(actual = actual, expected = expected)
    }

    @Test
    fun insertsContactWithOrganization(): Unit = runBlocking {
        store.execute {
            insert(
                MutableContact().apply {
                    organization = "Acme"
                    jobTitle = "Member"
                }
            )
        }

        val actual = store.fetchContacts(columnsToFetch = listOf(Organization)).first()
        val expected = contact(
            displayName = "Acme",
            organization = "Acme",
            jobTitle = "Member",
            columns = listOf(Organization)
        )

        assertOnlyContact(actual = actual, expected = expected)
    }

    @Test
    fun insertsContactWithIm(): Unit = runBlocking {
        store.execute {
            insert {
                imAddress(
                    address = "ImAddress",
                    protocol = "protocol",
                    label = LocationHome
                )
            }
        }

        val actual = store.fetchContacts(columnsToFetch = listOf(ImAddresses)).first()
        val expected = contact(
            imAddresses = listOf(
                LabeledValue(
                    ImAddress(raw = "ImAddress", protocol = "protocol"),
                    LocationHome,
                    id = 0
                )
            ),
            columns = listOf(ImAddresses)
        )

        assertOnlyContact(actual = actual, expected = expected)
    }
}
