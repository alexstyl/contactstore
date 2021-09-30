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
class ContactStoreQueryTest : ContactStoreTestBase() {

    @Before
    override fun before(): Unit = runBlocking {
        super.before()
        store.execute(SaveRequest().apply {
            insert(
                MutableContact().apply {
                    firstName = "Paolo"
                    lastName = "Melendez"
                    phones.add(
                        LabeledValue(
                            PhoneNumber(
                                "555"
                            ), Label.LocationHome
                        )
                    )
                }
            )
            insert(
                MutableContact().apply {
                    firstName = "Kim"
                    lastName = "Clay"
                    mails.add(
                        LabeledValue(
                            MailAddress(
                                "hi@mail.com"
                            ), Label.LocationHome
                        )
                    )
                }
            )
        })
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

    @Test
    fun searchForPhoneNumber(): Unit = runBlocking {
        val actual =
            store.fetchContacts(
                ContactPredicate.PhoneLookup(
                    PhoneNumber(
                        "555"
                    )
                )
            ).first()
        val expected = listOf(
            paoloMelendez()
        )
        assertThat(actual, equalTo(expected))
    }


    @Test
    fun searchForMailAddress(): Unit = runBlocking {
        val actual =
            store.fetchContacts(
                ContactPredicate.MailLookup(
                    MailAddress(
                        "hi@mail.com"
                    )
                )
            ).first()
        val expected = listOf(kimClay())

        assertThat(actual, equalTo(expected))
    }

    @Test
    fun lookupContactByName() = runBlocking {
        val actual = store.fetchContacts(NameLookup("Melendez")).first()
        val expected = listOf(paoloMelendez())

        assertThat(actual, equalTo(expected))
    }
}
