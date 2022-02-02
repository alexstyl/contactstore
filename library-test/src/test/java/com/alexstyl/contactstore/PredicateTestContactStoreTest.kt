package com.alexstyl.contactstore

import com.alexstyl.contactstore.ContactPredicate.ContactLookup
import com.alexstyl.contactstore.ContactPredicate.MailLookup
import com.alexstyl.contactstore.ContactPredicate.NameLookup
import com.alexstyl.contactstore.ContactPredicate.PhoneLookup
import com.alexstyl.contactstore.test.TestContactStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalContactStoreApi
@RunWith(RobolectricTestRunner::class)
internal class PredicateTestContactStoreTest {
    @Test
    fun `phone lookup`(): Unit = runBlocking {
        val store = TestContactStore(
            contactsSnapshot = listOf(
                SnapshotFixtures.PAOLO_MELENDEZ
            )
        )

        val actual = store.fetchContacts(
            predicate = PhoneLookup(PhoneNumber("55"))
        ).first()

        assertThat(actual).containsOnly(CONTACT)
    }

    @Test
    fun `name lookup`(): Unit = runBlocking {
        val store = TestContactStore(
            contactsSnapshot = listOf(
                SnapshotFixtures.PAOLO_MELENDEZ
            )
        )

        val actual = store.fetchContacts(
            predicate = NameLookup("Pao")
        ).first()

        assertThat(actual).containsOnly(CONTACT)
    }

    @Test
    fun `contact lookup`(): Unit = runBlocking {
        val store = TestContactStore(
            contactsSnapshot = listOf(
                SnapshotFixtures.PAOLO_MELENDEZ
            )
        )

        val actual = store.fetchContacts(
            predicate = ContactLookup(inContactIds = listOf(0L))
        ).first()

        assertThat(actual).containsOnly(CONTACT)
    }

    @Test
    fun `mail lookup`(): Unit = runBlocking {
        val store = TestContactStore(
            contactsSnapshot = listOf(
                SnapshotFixtures.PAOLO_MELENDEZ
            )
        )

        val actual = store.fetchContacts(
            predicate = MailLookup(MailAddress("hi"))
        ).first()

        assertThat(actual).containsOnly(CONTACT)
    }


    private companion object {
        val CONTACT = PartialContact(
            contactId = ContactFixtures.PAOLO_MELENDEZ.contactId,
            displayName = ContactFixtures.PAOLO_MELENDEZ.displayName,
            isStarred = ContactFixtures.PAOLO_MELENDEZ.isStarred,
            columns = emptyList(),
            lookupKey = null,
        )
    }
}
