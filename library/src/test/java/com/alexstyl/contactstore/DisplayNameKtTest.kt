package com.alexstyl.contactstore

import android.provider.ContactsContract
import com.alexstyl.contactstore.ContactColumn.Mails
import com.alexstyl.contactstore.ContactColumn.Names
import com.alexstyl.contactstore.ContactColumn.Nickname
import com.alexstyl.contactstore.ContactColumn.Organization
import com.alexstyl.contactstore.ContactColumn.Phones
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

internal class DisplayNameKtTest {

    @Test
    fun `returns empty when no columns are present`() {
        listOf(
            {
                mutableContact(
                    mails = listOf(LabeledValue(MailAddress("mail@mail.com"), Label.LocationHome))
                )
            },
            {
                mutableContact(
                    phones = listOf(LabeledValue(PhoneNumber("555"), Label.LocationHome))
                )
            },
            { mutableContact(organization = "Organization") },
            { mutableContact(jobTitle = "Organization") },
            { mutableContact(nickname = "Nickname") },
            { mutableContact(phoneticFirstName = "PhoneticFirst") },
            { mutableContact(phoneticMiddleName = "PhoneticMiddle") },
            { mutableContact(phoneticLastName = "PhoneticLast") },
            { mutableContact(prefix = "prefix") },
            { mutableContact(firstName = "FirstName") },
            { mutableContact(middleName = "MiddleName") },
            { mutableContact(lastName = "LastName") },
            { mutableContact(suffix = "Suffix") },
        ).forEach { createContact ->
            val contact = createContact()
            val actual = contact.displayName()
            assertThat(actual).isEqualTo("")
        }
    }

    @Test
    fun `returns email`() {
        val contact = mutableContact(
            mails = listOf(
                LabeledValue(MailAddress("mail@mail.com"), Label.LocationHome)
            ),
            columns = listOf(Mails)
        )

        val actual = contact.displayName()

        assertThat(actual).isEqualTo("mail@mail.com")
    }

    @Test
    fun `returns phones over mails`() {
        val contact = mutableContact(
            mails = listOf(
                LabeledValue(MailAddress("mail@mail.com"), Label.LocationHome)
            ),
            phones = listOf(LabeledValue(PhoneNumber("555"), Label.LocationHome)),
            columns = listOf(Mails, Phones)
        )

        val actual = contact.displayName()

        assertThat(actual).isEqualTo("555")
    }

    @Test
    fun `returns jobTitle over phones`() {
        val contact = mutableContact(
            phones = listOf(LabeledValue(PhoneNumber("555"), Label.LocationHome)),
            jobTitle = "jobTitle",
            columns = listOf(Phones, Organization)
        )

        val actual = contact.displayName()

        assertThat(actual).isEqualTo("jobTitle")
    }

    @Test
    fun `returns organization over jobTitle`() {
        val contact = mutableContact(
            jobTitle = "jobTitle",
            organization = "organization",
            columns = listOf(Phones, Organization)
        )

        val actual = contact.displayName()

        assertThat(actual).isEqualTo("organization")
    }

    @Test
    fun `returns nickname over organization`() {
        val contact = mutableContact(
            organization = "organization",
            nickname = "nickname",
            columns = listOf(Organization, Nickname)
        )

        val actual = contact.displayName()

        assertThat(actual).isEqualTo("nickname")
    }

    @Test
    fun `returns phonetic name over nickname`() {
        val contact = mutableContact(
            nickname = "nickname",
            phoneticFirstName = "PhoneticFirst",
            phoneticMiddleName = "PhoneticMiddle",
            phoneticLastName = "PhoneticLast",
            columns = listOf(Nickname, Names)
        )

        val actual = contact.displayName()

        assertThat(actual).isEqualTo("PhoneticFirst PhoneticMiddle PhoneticLast")
    }

    @Test
    fun `returns name over phonetic name`() {
        val contact = mutableContact(
            phoneticFirstName = "PhoneticFirst",
            phoneticMiddleName = "PhoneticMiddle",
            phoneticLastName = "PhoneticLast",
            prefix = "Prefix",
            firstName = "First",
            middleName = "Middle",
            lastName = "Last",
            suffix = "Suffix",
            columns = listOf(Names)
        )

        val actual = contact.displayName()

        assertThat(actual).isEqualTo("Prefix First Middle Last, Suffix")
    }

    private fun mutableContact(
        mails: List<LabeledValue<MailAddress>> = emptyList(),
        phones: List<LabeledValue<PhoneNumber>> = emptyList(),
        columns: List<ContactColumn> = emptyList(),
        organization: String? = null,
        jobTitle: String? = null,
        nickname: String? = null,
        phoneticFirstName: String? = null,
        phoneticLastName: String? = null,
        prefix: String? = null,
        firstName: String? = null,
        middleName: String? = null,
        lastName: String? = null,
        phoneticMiddleName: String? = null,
        suffix: String? = null,
    ): MutableContact {
        return MutableContact(
            contactId = -1L,
            lookupKey = null,
            firstName = firstName,
            organization = organization,
            jobTitle = jobTitle,
            lastName = lastName,
            isStarred = false,
            imageData = null,
            phones = phones.toMutableList(),
            mails = mails.toMutableList(),
            events = mutableListOf(),
            postalAddresses = mutableListOf(),
            webAddresses = mutableListOf(),
            sipAddresses = mutableListOf(),
            imAddresses = mutableListOf(),
            relations = mutableListOf(),
            note = null,
            columns = columns,
            middleName = middleName,
            prefix = prefix,
            suffix = suffix,
            phoneticNameStyle = ContactsContract.PhoneticNameStyle.UNDEFINED,
            fullNameStyle = ContactsContract.FullNameStyle.UNDEFINED,
            phoneticFirstName = phoneticFirstName,
            phoneticLastName = phoneticLastName,
            phoneticMiddleName = phoneticMiddleName,
            groups = mutableListOf(),
            nickname = nickname,
            customDataItems = emptyList()
        )
    }
}