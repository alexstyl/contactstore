package com.alexstyl.contactstore

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

@ExperimentalContactStoreApi
class ExecuteTestContactStoreTest {
    @Test
    fun `inserts contact`(): Unit = runBlocking {
        val store = TestContactStore(
            contactsSnapshot = emptyList()
        )

        store.execute(
            SaveRequest().apply {
                insert(ContactFixtures.PAOLO_MELENDEZ.mutableCopy())
            }
        )
        val actual = store.fetchContacts(
            columnsToFetch = ContactColumn.values().toList()
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

        store.execute(
            SaveRequest().apply {
                delete(paoloMelendez().contactId)
            }
        )
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

        store.execute(
            SaveRequest().apply {
                update(MutableContact().apply {
                    contactId = SNAPSHOT_PAOLO.contactId
                    firstName = "Peter"
                })
            }
        )
        val actual = store.fetchContacts().first()
        assertThat(actual).containsOnly(
            PartialContact(
                contactId = SNAPSHOT_PAOLO.contactId,
                displayName = "Prefix Peter Mid Melendez, Suffix",
                isStarred = false,
                columns = emptyList(),
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
    )

    private fun paoloMelendez() = PartialContact(
        contactId = 0L,
        displayName = "Paolo Melendez",
        isStarred = false,
        columns = emptyList(),
    )
}
