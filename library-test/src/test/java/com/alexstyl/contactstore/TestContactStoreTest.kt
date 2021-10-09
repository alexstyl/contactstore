package com.alexstyl.contactstore

import kotlinx.coroutines.runBlocking
import org.junit.Test

class TestContactStoreTest {
    @Test
    fun `fetches contacts in snapshot`(): Unit = runBlocking {
        val store = TestContactStore(
            databaseSnapshot = listOf(
                PartialContact(
                    contactId = 0L,
                    displayName = "Paolo Melendez",
                    isStarred = false,
                    columns = emptyList(),
                )
            )
        )

        store.fetchContacts()
    }
}