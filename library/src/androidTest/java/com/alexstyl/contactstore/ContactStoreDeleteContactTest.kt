package com.alexstyl.contactstore

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

@ExperimentalCoroutinesApi
class ContactStoreDeleteContactTest : ContactStoreTestBase() {

    @Test
    fun deletesContact(): Unit = runBlocking {
        store.execute(SaveRequest().apply {
            insert(
                MutableContact().apply {
                    firstName = "Paolo"
                    lastName = "Melendez"
                }
            )
            insert(
                MutableContact().apply {
                    firstName = "Kim"
                    lastName = "Clay"
                }
            )
            insert(
                MutableContact().apply {
                    firstName = "David"
                    lastName = "Jones"
                }
            )
        })

        val contactsBefore = store.fetchContacts().first()
        val contactToDelete = contactsBefore.first()

        store.execute(SaveRequest().apply {
            delete(contactId = contactToDelete.contactId)
        })

        val actual = store.fetchContacts().first()
        val expected = contactsBefore - contactToDelete

        assertThat(actual, equalTo(expected))
    }
}
