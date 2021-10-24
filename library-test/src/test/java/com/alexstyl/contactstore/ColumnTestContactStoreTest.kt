package com.alexstyl.contactstore

import com.alexstyl.contactstore.ContactColumn.GroupMemberships
import com.alexstyl.contactstore.ContactColumn.Image
import com.alexstyl.contactstore.ContactColumn.Mails
import com.alexstyl.contactstore.ContactColumn.Names
import com.alexstyl.contactstore.ContactColumn.Nickname
import com.alexstyl.contactstore.ContactColumn.Note
import com.alexstyl.contactstore.ContactColumn.Organization
import com.alexstyl.contactstore.ContactColumn.Phones
import com.alexstyl.contactstore.ContactColumn.PostalAddresses
import com.alexstyl.contactstore.ContactColumn.WebAddresses
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
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

        assertThat(actual).containsOnly(
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
            columnsToFetch = listOf(Names)
        ).first()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(Names),
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
            columnsToFetch = listOf(Phones)
        ).first()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(Phones),
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
            columnsToFetch = listOf(Mails)
        ).first()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(Mails),
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
            columnsToFetch = listOf(Organization)
        ).first()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(Organization),
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
            columnsToFetch = listOf(Image)
        ).first()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(Image),
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
            columnsToFetch = listOf(Note)
        ).first()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(Note),
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
            columnsToFetch = listOf(PostalAddresses)
        ).first()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(PostalAddresses),
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
            columnsToFetch = listOf(Nickname)
        ).first()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(Nickname),
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
            columnsToFetch = listOf(WebAddresses)
        ).first()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(WebAddresses),
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
            columnsToFetch = listOf(GroupMemberships)
        ).first()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(GroupMemberships),
                groups = listOf(
                    GroupMembership(groupId = 10)
                )
            )
        )
    }
}
