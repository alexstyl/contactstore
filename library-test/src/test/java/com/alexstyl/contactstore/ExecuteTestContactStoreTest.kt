package com.alexstyl.contactstore

import com.alexstyl.contactstore.test.TestContactStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalContactStoreApi
@RunWith(RobolectricTestRunner::class)
internal class ExecuteTestContactStoreTest {
    @Test
    fun `inserts contact`(): Unit = runBlocking {
        val store = TestContactStore(
            contactsSnapshot = emptyList()
        )

        store.execute {
            insert(ContactFixtures.PAOLO_MELENDEZ.mutableCopy())
        }
        val actual = store.fetchContacts(
            columnsToFetch = standardColumns()
        ).first()
        assertThat(actual).containsOnly(
            ContactFixtures.PAOLO_MELENDEZ
        )
    }

    @Test
    fun `deletes contact`(): Unit = runBlocking {
        val store = TestContactStore(
            contactsSnapshot = listOf(
                SNAPSHOT_PAOLO,
                SNAPSHOT_KIM
            )
        )

        store.execute {
            delete(paoloMelendez().contactId)
        }
        val actual = store.fetchContacts().first()
        assertThat(actual).containsOnly(
            kimClay()
        )
    }

    @Test
    fun `update contact`(): Unit = runBlocking {
        val store = TestContactStore(
            contactsSnapshot = listOf(
                SNAPSHOT_PAOLO,
                SNAPSHOT_KIM
            )
        )

        store.execute {
            update(MutableContact().apply {
                contactId = SNAPSHOT_PAOLO.contactId
                firstName = "Peter"
            })
        }

        val actual = store.fetchContacts().first()
        assertThat(actual).containsOnly(
            PartialContact(
                contactId = SNAPSHOT_PAOLO.contactId,
                displayName = "Prefix Peter Mid Melendez, Suffix",
                isStarred = false,
                columns = emptyList(),
                lookupKey = null,
            ),
            kimClay()
        )
    }

    private companion object {
        val SNAPSHOT_KIM = SnapshotFixtures.KIM_CLAY
        val SNAPSHOT_PAOLO = SnapshotFixtures.PAOLO_MELENDEZ
    }

    private fun kimClay() = PartialContact(
        contactId = 1L,
        displayName = "Kim Clay",
        isStarred = false,
        columns = emptyList(),
        lookupKey = null,
    )

    private fun paoloMelendez() = PartialContact(
        contactId = 0L,
        displayName = "Paolo Melendez",
        isStarred = false,
        columns = emptyList(),
        lookupKey = null,
    )
}
