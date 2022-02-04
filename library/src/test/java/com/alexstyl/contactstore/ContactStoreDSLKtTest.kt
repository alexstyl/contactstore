package com.alexstyl.contactstore

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class ContactStoreDSLKtTest {

    @Test
    fun insert(): Unit = runBlocking {
        val store = contactStore()
        store.execute {
            insert {
                prefix = "pref"
                firstName = "Paolo"
                middleName = "Mid"
                lastName = "Melendez"
                suffix = "Suf"
                note = "Note to self"
                jobTitle = "Job Title"
                organization = "Org"

                imageData = ImageData("image data".toByteArray())
                isStarred = false
                phone(
                    number = "555-11-23",
                    label = Label.LocationHome
                )

                mail(
                    address = "paolo@paolo.com",
                    label = Label.LocationWork
                )
                event(
                    dayOfMonth = 3,
                    month = 12,
                    year = 2021,
                    label = Label.DateBirthday
                )

                postalAddress(
                    street = "85 Somewhere Str",
                    label = Label.LocationHome
                )

                webAddress(
                    address = Uri.parse("www.paolo.com"),
                    label = Label.LocationWork
                )
                groupMembership(groupId = 123)
            }
        }
        assertThat(store.request).isEqualTo(
            SaveRequest().apply {
                insert(MutableContact().apply {
                    prefix = "pref"
                    firstName = "Paolo"
                    middleName = "Mid"
                    lastName = "Melendez"
                    suffix = "Suf"
                    note = Note("Note to self")
                    jobTitle = "Job Title"
                    organization = "Org"

                    imageData = ImageData("image data".toByteArray())
                    isStarred = false
                    phones.add(
                        LabeledValue(
                            value = PhoneNumber("555-11-23"),
                            label = Label.LocationHome
                        )
                    )
                    mails.add(
                        LabeledValue(
                            value = MailAddress("paolo@paolo.com"),
                            label = Label.LocationWork
                        )
                    )
                    events.add(
                        LabeledValue(
                            value = EventDate(
                                dayOfMonth = 3,
                                month = 12,
                                year = 2021,
                            ),
                            label = Label.DateBirthday
                        )
                    )

                    postalAddresses.add(
                        LabeledValue(
                            PostalAddress(street = "85 Somewhere Str"),
                            Label.LocationHome
                        )
                    )

                    webAddresses.add(
                        LabeledValue(
                            value = WebAddress(Uri.parse("www.paolo.com")),
                            label = Label.LocationWork
                        )
                    )
                    groups.add(
                        GroupMembership(123)
                    )
                })
            }
        )
    }

    private fun contactStore(): TestContactStore {
        return TestContactStore()
    }

    private class TestContactStore : ContactStore {

        var request: SaveRequest? = null

        override suspend fun execute(request: SaveRequest) {
            this.request = request
        }

        override suspend fun execute(request: SaveRequest.() -> Unit) {
            this.request = SaveRequest().apply(request)
        }

        override fun fetchContacts(
            predicate: ContactPredicate?,
            columnsToFetch: List<ContactColumn>,
            displayNameStyle: DisplayNameStyle
        ): Flow<List<Contact>> {
            return emptyFlow()
        }

        override fun fetchContactGroups(predicate: GroupsPredicate?): Flow<List<ContactGroup>> {
            return emptyFlow()
        }
    }
}
