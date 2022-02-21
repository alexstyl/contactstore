package com.alexstyl.contactstore

import android.net.Uri
import com.alexstyl.contactstore.Label.DateBirthday
import com.alexstyl.contactstore.Label.LocationHome
import com.alexstyl.contactstore.Label.PhoneNumberMobile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test

@ExperimentalCoroutinesApi
internal class InsertContactToAccountTest : ContactStoreTestBase() {

    @Test
    fun insertingContactToAccountAddsAllItemsToTheSameAccount(): Unit = runBlocking {
        val account = InternetAccount("test@test.com", "test.com")
        store.execute {
            insert(account) {
                phone("555", PhoneNumberMobile)
                mail("555@mail.com", LocationHome)
                event(dayOfMonth = 1, month = 1, year = 2021, DateBirthday)
                postalAddress(
                    street = "Somestreet",
                    poBox = "12345",
                    neighborhood = "Hood",
                    city = "City",
                    region = "",
                    postCode = "",
                    country = "",
                    label = LocationHome
                )
                webAddress(Uri.parse("https://acme.corp"), Label.WebsiteHomePage)
                imAddress(
                    address = "ImAddress",
                    protocol = "protocol",
                    label = LocationHome
                )
                relation(name = "Person", label = Label.PhoneNumberAssistant)
                sipAddress(address = "123", label = LocationHome)
            }
        }

        val actual = store.fetchContacts(columnsToFetch = allContactColumns()).blockingGet()
        val expected = contact(
            displayName = "555",
            columns = allContactColumns(),
            phones = listOf(
                LabeledValue(PhoneNumber("555"), PhoneNumberMobile, null, account)
            ),
            mails = listOf(
                LabeledValue(MailAddress("555@mail.com"), LocationHome, null, account)
            ),
            events = listOf(
                LabeledValue(EventDate(1, 1, 2021), DateBirthday, null, account)
            ),
            postalAddresses = listOf(
                LabeledValue(
                    PostalAddress(
                        street = "Somestreet",
                        poBox = "12345",
                        neighborhood = "Hood",
                        city = "City",
                        region = "",
                        postCode = "",
                        country = ""
                    ),
                    LocationHome,
                    null,
                    account
                )
            ),
            webAddresses = listOf(
                LabeledValue(
                    WebAddress(Uri.parse("https://acme.corp")),
                    Label.WebsiteHomePage,
                    null,
                    account
                )
            ),
            imAddresses = listOf(
                LabeledValue(
                    ImAddress(raw = "ImAddress", protocol = "protocol"),
                    LocationHome,
                    null,
                    account
                )
            ),
            relations = listOf(
                LabeledValue(Relation(name = "Person"), Label.PhoneNumberAssistant, null, account)
            ),
            sipAddresses = listOf(
                LabeledValue(SipAddress(raw = "123"), LocationHome, null, account)
            ),
        )
        assertOnlyContact(actual = actual, expected = expected)
    }
}
