package com.alexstyl.contactstore

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

@ExperimentalCoroutinesApi
internal class ContactStoreDeleteContactTest : ContactStoreTestBase() {

    @Test
    fun deletesContact(): Unit = runBlocking {
        store.execute {
            insert {
                firstName = "Paolo"
                lastName = "Melendez"
            }
            insert {
                firstName = "Kim"
                lastName = "Clay"
            }
            insert {
                firstName = "David"
                lastName = "Jones"
            }
        }

        val contactsBefore = store.fetchContacts().first()
        val contactToDelete = contactsBefore.first()

        store.execute {
            delete(contactId = contactToDelete.contactId)
        }

        val actual = store.fetchContacts().first()
        val expected = contactsBefore - contactToDelete

        assertThat(actual, equalTo(expected))
    }
}
