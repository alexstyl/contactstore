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
import com.alexstyl.contactstore.Label.DateBirthday
import com.alexstyl.contactstore.Label.LocationHome
import com.alexstyl.contactstore.Label.PhoneNumberMobile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test

@ExperimentalCoroutinesApi
internal class ContactStoreInsertContactTest : ContactStoreTestBase() {

    @Test
    fun insertsContactWithNames(): Unit = runBlocking {
        store.execute {
            insert {
                prefix = "A."
                firstName = "Paolo"
                middleName = "M."
                lastName = "Melendez"
                suffix = "Z."
            }
        }

        val actual = store.fetchContacts(columnsToFetch = listOf(Names)).blockingGet()
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
            insert {
                phone("555", PhoneNumberMobile)
            }
        }

        val actual = store.fetchContacts(columnsToFetch = listOf(Phones)).blockingGet()
        val expected = contact(
            displayName = "555",
            columns = listOf(Phones),
            phones = listOf(
                LabeledValue(
                    PhoneNumber("555"),
                    PhoneNumberMobile,
                )
            )
        )

        assertOnlyContact(actual = actual, expected = expected)
    }

    @Test
    fun insertsContactWithMails(): Unit = runBlocking {
        store.execute {
            insert {
                mail("555@mail.com", LocationHome)
            }
        }

        val actual = store.fetchContacts(columnsToFetch = listOf(Mails)).blockingGet()
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
            insert {
                event(dayOfMonth = 1, month = 1, year = 2021, DateBirthday)
            }
        }

        val actual = store.fetchContacts(columnsToFetch = listOf(Events)).blockingGet()
        val expected = contact(
            columns = listOf(Events),
            events = listOf(
                LabeledValue(EventDate(1, 1, 2021), DateBirthday)
            )
        )

        assertOnlyContact(actual = actual, expected = expected)
    }

    @Test
    fun insertsContactWithPostalAddress(): Unit = runBlocking {
        store.execute {
            insert {
                postalAddress(
                    street = "Somestreet",
                    poBox = "12345",
                    neighborhood = "Hood",
                    city = "City",
                    region = "",
                    postCode = "",
                    country = "",
                    label = LocationHome
                )
            }
        }

        val actual = store.fetchContacts(columnsToFetch = listOf(PostalAddresses)).blockingGet()
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
                    ),
                    LocationHome,
                )
            )
        )

        assertOnlyContact(actual = actual, expected = expected)
    }

    @Test
    fun insertsContactWithNote(): Unit = runBlocking {
        store.execute {
            insert {
                note = "Cool guy"
            }
        }

        val actual = store.fetchContacts(columnsToFetch = listOf(Note)).blockingGet()
        val expected = contact(
            columns = listOf(Note),
            note = Note("Cool guy")
        )

        assertOnlyContact(actual = actual, expected = expected)
    }

    @Test
    fun insertsContactWithWebAddresses(): Unit = runBlocking {
        store.execute {
            insert {
                webAddress(Uri.parse("https://acme.corp"), Label.WebsiteHomePage)
            }
        }

        val actual = store.fetchContacts(columnsToFetch = listOf(WebAddresses)).blockingGet()
        val expected = contact(
            columns = listOf(WebAddresses),
            webAddresses = listOf(
                LabeledValue(
                    WebAddress(Uri.parse("https://acme.corp")),
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
            insert {
                organization = "Acme"
                jobTitle = "Member"
            }
        }

        val actual = store.fetchContacts(columnsToFetch = listOf(Organization)).blockingGet()
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

        val actual = store.fetchContacts(columnsToFetch = listOf(ImAddresses)).blockingGet()
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

    @Test
    fun insertsContactWithRelation(): Unit = runBlocking {
        store.execute {
            insert {
                relation(name = "Person", label = Label.PhoneNumberAssistant)
            }
        }

        val actual = store.fetchContacts(columnsToFetch = listOf(Relations)).blockingGet()
        val expected = contact(
            relations = listOf(
                LabeledValue(Relation(name = "Person"), Label.PhoneNumberAssistant)
            ),
            columns = listOf(Relations)
        )

        assertOnlyContact(actual = actual, expected = expected)
    }

    @Test
    fun insertsContactWithSip(): Unit = runBlocking {
        store.execute {
            insert {
                sipAddress(address = "123", label = LocationHome)
            }
        }

        val actual = store.fetchContacts(columnsToFetch = listOf(SipAddresses)).blockingGet()
        val expected = contact(
            sipAddresses = listOf(
                LabeledValue(SipAddress(raw = "123"), LocationHome)
            ),
            columns = listOf(SipAddresses)
        )

        assertOnlyContact(actual = actual, expected = expected)
    }
}
