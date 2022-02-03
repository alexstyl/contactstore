package com.alexstyl.contactstore

import com.alexstyl.contactstore.ContactPredicate.NameLookup
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
        val actual = store.fetchContacts(
            ContactPredicate.PhoneLookup(PhoneNumber("555"))
        ).first()
        val expected = listOf(
            paoloMelendez()
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun lookupByMail(): Unit = runBlocking {
        val actual = store.fetchContacts(
            ContactPredicate.MailLookup(MailAddress("hi@mail.com"))
        ).first()
        val expected = listOf(kimClay())

        assertThat(actual, equalTo(expected))
    }

    @Test
    fun lookupByName() = runBlocking {
        val actual = store.fetchContacts(NameLookup("Melendez")).first()
        val expected = listOf(paoloMelendez())

        assertThat(actual, equalTo(expected))
    }

    private suspend fun paoloMelendez(): Contact {
        return store.fetchContacts().first().first {
            it.displayName == "Paolo Melendez"
        }
    }

    private suspend fun kimClay(): Contact {
        return store.fetchContacts().first().first {
            it.displayName == "Kim Clay"
        }
    }
}
