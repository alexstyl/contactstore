package com.alexstyl.contactstore

import android.app.Application
import android.provider.ContactsContract
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.flow.first
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule


internal abstract class ContactStoreTestBase {

    @get:Rule
    var grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.READ_CONTACTS,
        android.Manifest.permission.WRITE_CONTACTS
    )

    suspend fun assertContactUpdated(editedContact: MutableContact) {
        store.execute { update(editedContact) }
        val actual = store.fetchContacts(
            predicate = ContactPredicate.ContactLookup(inContactIds = listOf(editedContact.contactId)),
            columnsToFetch = editedContact.columns
        ).first().first()

        assertThat(actual, equalTo(editedContact))
    }

    suspend fun assertContactUpdatedNoId(expected: MutableContact) {
        store.execute { update(expected) }
        val actual = store.fetchContacts(
            predicate = ContactPredicate.ContactLookup(inContactIds = listOf(expected.contactId)),
            columnsToFetch = expected.columns
        ).first().first()

        assertThat(actual, equalContents(expected))
    }

    protected lateinit var store: ContactStore

    @Before
    open fun before() {
        deleteAllContacts()
        store = ContactStore.newInstance(context)
    }

    private fun deleteAllContacts() {
        Log.w(javaClass::class.simpleName, "CLEANING UP CONTACTS")
        val contentResolver = context.contentResolver
        val result = contentResolver.delete(ContactsContract.RawContacts.CONTENT_URI, null, null)
        Log.w(javaClass::class.simpleName, "Deleted $result contacts")
    }

    protected open val context: Application = ApplicationProvider.getApplicationContext()

    protected fun contact(
        displayName: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        organization: String? = null,
        jobTitle: String? = null,
        columns: List<ContactColumn> = emptyList(),
        phones: List<LabeledValue<PhoneNumber>> = emptyList(),
        mails: List<LabeledValue<MailAddress>> = emptyList(),
        postalAddresses: List<LabeledValue<PostalAddress>> = emptyList(),
        events: List<LabeledValue<EventDate>> = emptyList(),
        webAddresses: List<LabeledValue<WebAddress>> = emptyList(),
        imAddresses: List<LabeledValue<ImAddress>> = emptyList(),
        sipAddresses: List<LabeledValue<SipAddress>> = emptyList(),
        relations : List<LabeledValue<Relation>> = emptyList(),
        note: Note? = null,
        prefix: String? = null,
        middleName: String? = null,
        suffix: String? = null
    ): PartialContact {
        return PartialContact(
            contactId = IGNORED,
            lookupKey = null,
            displayName = displayName,
            prefix = prefix,
            middleName = middleName,
            suffix = suffix,
            organization = organization,
            jobTitle = jobTitle,
            firstName = firstName,
            lastName = lastName,
            events = events,
            postalAddresses = postalAddresses,
            sipAddresses = sipAddresses,
            note = note,
            columns = columns,
            webAddresses = webAddresses,
            imAddresses = imAddresses,
            phones = phones,
            mails = mails,
            isStarred = false,
            relations = relations
        )
    }

    suspend fun buildStoreContact(
        vararg withColumns: ContactColumn,
        contactBuilder: MutableContact.() -> Unit
    ): Contact {
        store.execute {
            insert(
                MutableContact().apply(contactBuilder)
            )
        }

        val contactsBefore = store.fetchContacts(columnsToFetch = withColumns.toList()).first()
        return contactsBefore.first()
    }


    protected fun assertOnlyContact(actual: List<Contact>, expected: Contact) {
        assertThat(actual.size, equalTo(1))
        assertThat(actual.first(), equalContents(expected))
    }

    private companion object {
        const val IGNORED: Long = -1
    }
}
