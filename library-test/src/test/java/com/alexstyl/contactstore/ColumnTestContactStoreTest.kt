package com.alexstyl.contactstore

import com.alexstyl.contactstore.ContactColumn.GROUP_MEMBERSHIPS
import com.alexstyl.contactstore.ContactColumn.IMAGE
import com.alexstyl.contactstore.ContactColumn.MAILS
import com.alexstyl.contactstore.ContactColumn.NAMES
import com.alexstyl.contactstore.ContactColumn.NICKNAME
import com.alexstyl.contactstore.ContactColumn.NOTE
import com.alexstyl.contactstore.ContactColumn.ORGANIZATION
import com.alexstyl.contactstore.ContactColumn.PHONES
import com.alexstyl.contactstore.ContactColumn.POSTAL_ADDRESSES
import com.alexstyl.contactstore.ContactColumn.WEB_ADDRESSES
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
            columnsToFetch = listOf(NAMES)
        ).first()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(NAMES),
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
            columnsToFetch = listOf(PHONES)
        ).first()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(PHONES),
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
            columnsToFetch = listOf(MAILS)
        ).first()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(MAILS),
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
            columnsToFetch = listOf(ORGANIZATION)
        ).first()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(ORGANIZATION),
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
            columnsToFetch = listOf(IMAGE)
        ).first()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(IMAGE),
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
            columnsToFetch = listOf(NOTE)
        ).first()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(NOTE),
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
            columnsToFetch = listOf(POSTAL_ADDRESSES)
        ).first()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(POSTAL_ADDRESSES),
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
            columnsToFetch = listOf(NICKNAME)
        ).first()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(NICKNAME),
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
            columnsToFetch = listOf(WEB_ADDRESSES)
        ).first()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(WEB_ADDRESSES),
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
            columnsToFetch = listOf(GROUP_MEMBERSHIPS)
        ).first()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(GROUP_MEMBERSHIPS),
                groups = listOf(
                    GroupMembership(groupId = 10)
                )
            )
        )
    }
}
