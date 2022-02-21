package com.alexstyl.contactstore.test

import android.net.Uri
import com.alexstyl.contactstore.ContactColumn
import com.alexstyl.contactstore.ExperimentalContactStoreApi
import com.alexstyl.contactstore.GroupMembership
import com.alexstyl.contactstore.ImageData
import com.alexstyl.contactstore.Label
import com.alexstyl.contactstore.LabeledValue
import com.alexstyl.contactstore.MailAddress
import com.alexstyl.contactstore.Note
import com.alexstyl.contactstore.PartialContact
import com.alexstyl.contactstore.PostalAddress
import com.alexstyl.contactstore.WebAddress
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalContactStoreApi
@RunWith(RobolectricTestRunner::class)
internal class ColumnTestContactStoreTest {
    @Test
    fun `fetches minimum details from snapshot`(): Unit = runBlocking {
        val store = TestContactStore(
            contactsSnapshot = listOf(
                SnapshotFixtures.PAOLO_MELENDEZ
            )
        )

        val actual = store.fetchContacts().blockingGet()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = emptyList(),
                lookupKey = null,
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
        ).blockingGet()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(ContactColumn.Names),
                prefix = "Prefix",
                firstName = "Paolo",
                middleName = "Mid",
                lastName = "Melendez",
                suffix = "Suffix",
                lookupKey = null,
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
        ).blockingGet()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(ContactColumn.Phones),
                phones = ContactFixtures.PAOLO_MELENDEZ.phones,
                lookupKey = null,
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
        ).blockingGet()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(ContactColumn.Mails),
                mails = listOf(
                    LabeledValue(MailAddress("hi@mail.com"), Label.LocationHome)
                ),
                lookupKey = null,
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
        ).blockingGet()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(ContactColumn.Organization),
                organization = "Organization",
                jobTitle = "Job Title",
                lookupKey = null,
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
        ).blockingGet()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(ContactColumn.Image),
                imageData = ImageData("imagedata".toByteArray()),
                lookupKey = null,
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
        ).blockingGet()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(ContactColumn.Note),
                note = Note("note"),
                lookupKey = null,
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
        ).blockingGet()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(ContactColumn.PostalAddresses),
                postalAddresses = listOf(
                    LabeledValue(PostalAddress("SomeStreet 55"), Label.LocationHome)
                ),
                lookupKey = null,
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
        ).blockingGet()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(ContactColumn.Nickname),
                nickname = "Nickname",
                lookupKey = null,
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
        ).blockingGet()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(ContactColumn.WebAddresses),
                webAddresses = listOf(
                    LabeledValue(WebAddress(Uri.parse("www.web.com")), Label.WebsiteHomePage)
                ),
                lookupKey = null,
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
        ).blockingGet()

        assertThat(actual).containsOnly(
            PartialContact(
                contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
                displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
                isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
                columns = listOf(ContactColumn.GroupMemberships),
                groups = listOf(
                    GroupMembership(groupId = 10)
                ),
                lookupKey = null,
            )
        )
    }
}