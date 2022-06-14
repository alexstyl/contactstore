package com.alexstyl.contactstore

import com.alexstyl.contactstore.ContactPredicate.MailLookup
import com.alexstyl.contactstore.ContactPredicate.ContactLookup
import com.alexstyl.contactstore.ContactPredicate.PhoneLookup
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
internal class ContactStoreLookupTest : ContactStoreTestBase() {

    @Before
    override fun before(): Unit = runBlocking {
        super.before()
        store.execute {
            insert {
                firstName = "Paolo"
                lastName = "Melendez"
                phone("555", Label.LocationHome)
            }
            insert {
                firstName = "Kim"
                lastName = "Clay"
                mail("hi@mail.com", Label.LocationHome)
            }
        }
    }

    @Test
    fun lookupByPhoneNumber(): Unit = runBlocking {
        val actual = store.fetchContacts(PhoneLookup("555")).blockingGet()
        val expected = listOf(
            paoloMelendez()
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun lookupByMail(): Unit = runBlocking {
        val actual = store.fetchContacts(MailLookup("hi@mail.com")).blockingGet()
        val expected = listOf(kimClay())

        assertThat(actual, equalTo(expected))
    }

    @Test
    fun lookupByName() = runBlocking {
        val actual = store.fetchContacts(ContactLookup("Melendez")).blockingGet()
        val expected = listOf(paoloMelendez())

        assertThat(actual, equalTo(expected))
    }

    private suspend fun paoloMelendez(): Contact {
        return store.fetchContacts().blockingGet().first {
            it.displayName == "Paolo Melendez"
        }
    }

    private suspend fun kimClay(): Contact {
        return store.fetchContacts().blockingGet().first {
            it.displayName == "Kim Clay"
        }
    }
}
