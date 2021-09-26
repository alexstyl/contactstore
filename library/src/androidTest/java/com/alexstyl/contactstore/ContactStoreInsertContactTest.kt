package com.alexstyl.contactstore

import com.alexstyl.contactstore.ContactColumn.EVENTS
import com.alexstyl.contactstore.ContactColumn.MAILS
import com.alexstyl.contactstore.ContactColumn.NAMES
import com.alexstyl.contactstore.ContactColumn.NOTE
import com.alexstyl.contactstore.ContactColumn.ORGANIZATION
import com.alexstyl.contactstore.ContactColumn.PHONES
import com.alexstyl.contactstore.ContactColumn.POSTAL_ADDRESSES
import com.alexstyl.contactstore.ContactColumn.WEB_ADDRESSES
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
        store.execute(SaveRequest().apply {
            insert(
                MutableContact().apply {
                    prefix = "A."
                    firstName = "Paolo"
                    middleName = "M."
                    lastName = "Melendez"
                    suffix = "Z."
                }
            )
        })

        val actual = store.fetchContacts(columnsToFetch = listOf(NAMES)).first()
        val expected = contact(
            displayName = "A. Paolo M. Melendez, Z.",
            prefix = "A.",
            firstName = "Paolo",
            middleName = "M.",
            lastName = "Melendez",
            suffix = "Z.",
            columns = listOf(NAMES)
        )

        assertOnlyContact(actual = actual, expected = expected)
    }

    @Test
    fun insertsContactWithPhones(): Unit = runBlocking {
        store.execute(SaveRequest().apply {
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
        })

        val actual = store.fetchContacts(columnsToFetch = listOf(PHONES)).first()
        val expected = contact(
            displayName = "555",
            columns = listOf(PHONES),
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
        store.execute(SaveRequest().apply {
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
        })

        val actual = store.fetchContacts(columnsToFetch = listOf(MAILS)).first()
        val expected = contact(
            displayName = "555@mail.com",
            columns = listOf(MAILS),
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
        store.execute(SaveRequest().apply {
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
        })

        val actual = store.fetchContacts(columnsToFetch = listOf(EVENTS)).first()
        val expected = contact(
            displayName = "",
            columns = listOf(EVENTS),
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
        store.execute(SaveRequest().apply {
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
        })

        val actual = store.fetchContacts(columnsToFetch = listOf(POSTAL_ADDRESSES)).first()
        val expected = contact(
            columns = listOf(POSTAL_ADDRESSES),
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
        store.execute(SaveRequest().apply {
            insert(
                MutableContact().apply {
                    note = Note("Cool guy")
                }
            )
        })

        val actual = store.fetchContacts(columnsToFetch = listOf(NOTE)).first()
        val expected = contact(
            columns = listOf(NOTE),
            note = Note("Cool guy")
        )

        assertOnlyContact(actual = actual, expected = expected)
    }

    @Test
    fun insertsContactWithWebAddresses(): Unit = runBlocking {
        store.execute(SaveRequest().apply {
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
        })

        val actual = store.fetchContacts(columnsToFetch = listOf(WEB_ADDRESSES)).first()
        val expected = contact(
            displayName = "",
            columns = listOf(WEB_ADDRESSES),
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
        store.execute(SaveRequest().apply {
            insert(
                MutableContact().apply {
                    organization = "Acme"
                    jobTitle = "Member"
                }
            )
        })

        val actual = store.fetchContacts(columnsToFetch = listOf(ORGANIZATION)).first()
        val expected = contact(
            displayName = "Acme",
            organization = "Acme",
            jobTitle = "Member",
            columns = listOf(ORGANIZATION)
        )

        assertOnlyContact(actual = actual, expected = expected)
    }
}
