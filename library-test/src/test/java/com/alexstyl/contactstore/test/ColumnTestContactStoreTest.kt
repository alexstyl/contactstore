package com.alexstyl.contactstore.test

import com.alexstyl.contactstore.ContactColumn
import com.alexstyl.contactstore.ContactFixtures
import com.alexstyl.contactstore.ExperimentalContactStoreApi
import com.alexstyl.contactstore.GroupMembership
import com.alexstyl.contactstore.ImageData
import com.alexstyl.contactstore.Label
import com.alexstyl.contactstore.LabeledValue
import com.alexstyl.contactstore.MailAddress
import com.alexstyl.contactstore.Note
import com.alexstyl.contactstore.PartialContact
import com.alexstyl.contactstore.PostalAddress
import com.alexstyl.contactstore.SnapshotFixtures
import com.alexstyl.contactstore.WebAddress
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.Test

@ExperimentalContactStoreApi
class ColumnTestContactStoreTest {
    @Test
    fun `fetches minimum details from snapshot`(): Unit = runBlocking {
        val store = TestContactStore(
            contactsSnapshot = listOf(
                SnapshotFixtures.PAOLO_MELENDEZ
            )
        )

        val actual = store.fetchContacts().first()

        Assertions.assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = emptyList(),
            )
        )
    }

    @Test
    fun `fetches names from snapshot`(): Unit = runBlocking {
        val store = TestContactStore(
            contactsSnapshot = listOf(
                SnapshotFixtures.PAOLO_MELENDEZ
            )
        )

        val actual = store.fetchContacts(
            columnsToFetch = listOf(ContactColumn.Names)
        ).first()

        Assertions.assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(ContactColumn.Names),
                prefix = "Prefix",
                firstName = "Paolo",
                middleName = "Mid",
                lastName = "Melendez",
                suffix = "Suffix"
            )
        )
    }

    @Test
    fun `fetches phones from snapshot`(): Unit = runBlocking {
        val store = TestContactStore(
            contactsSnapshot = listOf(
                SnapshotFixtures.PAOLO_MELENDEZ
            )
        )

        val actual = store.fetchContacts(
            columnsToFetch = listOf(ContactColumn.Phones)
        ).first()

        Assertions.assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(ContactColumn.Phones),
                phones = ContactFixtures.PAOLO_MELENDEZ.phones
            )
        )
    }

    @Test
    fun `fetches mails from snapshot`(): Unit = runBlocking {
        val store = TestContactStore(
            contactsSnapshot = listOf(
                SnapshotFixtures.PAOLO_MELENDEZ
            )
        )

        val actual = store.fetchContacts(
            columnsToFetch = listOf(ContactColumn.Mails)
        ).first()

        Assertions.assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(ContactColumn.Mails),
                mails = listOf(
                    LabeledValue(MailAddress("hi@mail.com"), Label.LocationHome)
                )
            )
        )
    }

    @Test
    fun `fetches organization from snapshot`(): Unit = runBlocking {
        val store = TestContactStore(
            contactsSnapshot = listOf(
                SnapshotFixtures.PAOLO_MELENDEZ
            )
        )

        val actual = store.fetchContacts(
            columnsToFetch = listOf(ContactColumn.Organization)
        ).first()

        Assertions.assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(ContactColumn.Organization),
                organization = "Organization",
                jobTitle = "Job Title"
            )
        )
    }

    @Test
    fun `fetches image data from snapshot`(): Unit = runBlocking {
        val store = TestContactStore(
            contactsSnapshot = listOf(
                SnapshotFixtures.PAOLO_MELENDEZ
            )
        )

        val actual = store.fetchContacts(
            columnsToFetch = listOf(ContactColumn.Image)
        ).first()

        Assertions.assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(ContactColumn.Image),
                imageData = ImageData("imagedata".toByteArray())
            )
        )
    }

    @Test
    fun `fetches note data from snapshot`(): Unit = runBlocking {
        val store = TestContactStore(
            contactsSnapshot = listOf(
                SnapshotFixtures.PAOLO_MELENDEZ
            )
        )

        val actual = store.fetchContacts(
            columnsToFetch = listOf(ContactColumn.Note)
        ).first()

        Assertions.assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(ContactColumn.Note),
                note = Note("note")
            )
        )
    }

    @Test
    fun `fetches postal addresses from snapshot`(): Unit = runBlocking {
        val store = TestContactStore(
            contactsSnapshot = listOf(
                SnapshotFixtures.PAOLO_MELENDEZ
            )
        )

        val actual = store.fetchContacts(
            columnsToFetch = listOf(ContactColumn.PostalAddresses)
        ).first()

        Assertions.assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(ContactColumn.PostalAddresses),
                postalAddresses = listOf(
                    LabeledValue(PostalAddress("SomeStreet 55"), Label.LocationHome)
                )
            )
        )
    }

    @Test
    fun `fetches nickname from snapshot`(): Unit = runBlocking {
        val store = TestContactStore(
            contactsSnapshot = listOf(
                SnapshotFixtures.PAOLO_MELENDEZ
            )
        )

        val actual = store.fetchContacts(
            columnsToFetch = listOf(ContactColumn.Nickname)
        ).first()

        Assertions.assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(ContactColumn.Nickname),
                nickname = "Nickname"
            )
        )
    }

    @Test
    fun `fetches web addresses from snapshot`(): Unit = runBlocking {
        val store = TestContactStore(
            contactsSnapshot = listOf(
                SnapshotFixtures.PAOLO_MELENDEZ
            )
        )

        val actual = store.fetchContacts(
            columnsToFetch = listOf(ContactColumn.WebAddresses)
        ).first()

        Assertions.assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(ContactColumn.WebAddresses),
                webAddresses = listOf(
                    LabeledValue(WebAddress("www.web.com"), Label.WebsiteHomePage)
                )
            )
        )
    }

    @Test
    fun `fetches groups from snapshot`(): Unit = runBlocking {
        val store = TestContactStore(
            contactsSnapshot = listOf(
                SnapshotFixtures.PAOLO_MELENDEZ
            )
        )

        val actual = store.fetchContacts(
            columnsToFetch = listOf(ContactColumn.GroupMemberships)
        ).first()

        Assertions.assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(ContactColumn.GroupMemberships),
                groups = listOf(
                    GroupMembership(groupId = 10)
                )
            )
        )
    }
}