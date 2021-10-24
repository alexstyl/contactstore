package com.alexstyl.contactstore.test

import android.provider.ContactsContract
import com.alexstyl.contactstore.Contact
import com.alexstyl.contactstore.ContactColumn
import com.alexstyl.contactstore.ContactOperation
import com.alexstyl.contactstore.ContactPredicate
import com.alexstyl.contactstore.ContactStore
import com.alexstyl.contactstore.ExperimentalContactStoreApi
import com.alexstyl.contactstore.MutableContact
import com.alexstyl.contactstore.PartialContact
import com.alexstyl.contactstore.SaveRequest
import com.alexstyl.contactstore.containsColumn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * An implementation of [ContactStore] usable for testing. The store holds a snapshot of contacts
 * that will be used for any operations on the store.
 *
 * You can initialize the store by providing a list of [StoredContact]s.
 * This can be used for emulating scenarios where a pre-populated store is needed.
 *
 * The implementation of this class tries to match the behavior of AOSP as much as possible.
 * Different OEMs might have altered the behavior of their ContactProvider and as a result different results might be returned.
 *
 * Do not use this class as a source of truth of how a real device will behave.
 */
@ExperimentalContactStoreApi
class TestContactStore(
    contactsSnapshot: List<StoredContact> = emptyList()
) : ContactStore {

    private val snapshot: MutableStateFlow<List<StoredContact>> = MutableStateFlow(contactsSnapshot)

    override suspend fun execute(request: SaveRequest) {
        request.requests.forEach { operation ->
            when (operation) {
                is ContactOperation.Delete -> deleteContact(withId = operation.contactId)
                is ContactOperation.Insert -> insertContact(operation.contact)
                is ContactOperation.Update -> updateContact(operation.contact)
            }
        }
    }

    private suspend fun deleteContact(withId: Long) {
        snapshot.emit(
            snapshot.value.dropWhile { it.contactId == withId }
        )
    }

    private suspend fun insertContact(contact: MutableContact) {
        val current = snapshot.value
        snapshot.emit(
            current.toMutableList()
                .apply {
                    add(
                        StoredContact(
                            contactId = current.size.toLong(),
                            isStarred = contact.isStarred,
                            prefix = contact.takeIfContains(ContactColumn.Names) { contact.prefix },
                            firstName = contact.takeIfContains(ContactColumn.Names) { contact.firstName },
                            middleName = contact.takeIfContains(ContactColumn.Names) { contact.middleName },
                            lastName = contact.takeIfContains(ContactColumn.Names) { contact.lastName },
                            suffix = contact.takeIfContains(ContactColumn.Names) { contact.suffix },
                            phoneticMiddleName = contact.takeIfContains(ContactColumn.Names) { contact.phoneticMiddleName },
                            phoneticFirstName = contact.takeIfContains(ContactColumn.Names) { contact.phoneticFirstName },
                            phoneticLastName = contact.takeIfContains(ContactColumn.Names) { contact.phoneticLastName },
                            phoneticNameStyle = contact.takeIfContains(ContactColumn.Names) { contact.phoneticNameStyle }
                                ?: ContactsContract.PhoneticNameStyle.UNDEFINED,
                            imageData = contact.takeIfContains(ContactColumn.Image) { contact.imageData },
                            organization = contact.takeIfContains(ContactColumn.Organization) { contact.organization },
                            jobTitle = contact.takeIfContains(ContactColumn.Organization) { contact.jobTitle },
                            webAddresses = contact.takeIfContains(ContactColumn.WebAddresses) { contact.webAddresses }
                                .orEmpty(),
                            phones = contact.takeIfContains(ContactColumn.Phones) { contact.phones }
                                .orEmpty(),
                            mails = contact.takeIfContains(ContactColumn.Mails) { contact.mails }
                                .orEmpty(),
                            events = contact.takeIfContains(ContactColumn.Events) { contact.events }
                                .orEmpty(),
                            postalAddresses = contact.takeIfContains(ContactColumn.PostalAddresses) { contact.postalAddresses }
                                .orEmpty(),
                            note = contact.takeIfContains(ContactColumn.Note) { contact.note },
                            nickname = contact.takeIfContains(ContactColumn.Nickname) { contact.nickname },
                            groups = contact
                                .takeIfContains(ContactColumn.GroupMemberships) { contact.groups }
                                .orEmpty(),
                            fullNameStyle = contact.takeIfContains(ContactColumn.Names) { contact.fullNameStyle }
                                ?: ContactsContract.FullNameStyle.UNDEFINED
                        )
                    )
                }
                .toList()
        )
    }

    private fun <T> Contact.takeIfContains(required: ContactColumn, function: () -> T?): T? {
        return if (containsColumn(required)) {
            function.invoke()
        } else {
            null
        }
    }

    private suspend fun updateContact(contact: MutableContact) {
        val currentContact = snapshot.value
            .find { it.contactId == contact.contactId } ?: return
        val updatedContact = currentContact.copy(
            firstName = contact.takeIfContains(ContactColumn.Names) { contact.firstName }
                ?: currentContact.firstName,
            isStarred = contact.isStarred,
            prefix = contact.takeIfContains(ContactColumn.Names) { contact.prefix }
                ?: currentContact.prefix,
            middleName = contact.takeIfContains(ContactColumn.Names) { contact.middleName }
                ?: currentContact.middleName,
            lastName = contact.takeIfContains(ContactColumn.Names) { contact.lastName }
                ?: currentContact.lastName,
            suffix = contact.takeIfContains(ContactColumn.Names) { contact.suffix }
                ?: currentContact.suffix,
            phoneticMiddleName = contact.takeIfContains(ContactColumn.Names) { contact.phoneticMiddleName }
                ?: currentContact.phoneticMiddleName,
            phoneticFirstName = contact.takeIfContains(ContactColumn.Names) { contact.phoneticFirstName }
                ?: currentContact.phoneticFirstName,
            phoneticLastName = contact.takeIfContains(ContactColumn.Names) { contact.phoneticLastName }
                ?: currentContact.phoneticLastName,
            phoneticNameStyle = contact.takeIfContains(ContactColumn.Names) { contact.phoneticNameStyle }
                ?: currentContact.phoneticNameStyle,
            imageData = contact.takeIfContains(ContactColumn.Image) { contact.imageData }
                ?: currentContact.imageData,
            organization = contact.takeIfContains(ContactColumn.Organization) { contact.organization }
                ?: currentContact.organization,
            jobTitle = contact.takeIfContains(ContactColumn.Organization) { contact.jobTitle }
                ?: currentContact.jobTitle,
            webAddresses = contact.takeIfContains(ContactColumn.WebAddresses) { contact.webAddresses }
                ?: currentContact.webAddresses,
            phones = contact.takeIfContains(ContactColumn.Phones) { contact.phones }
                ?: currentContact.phones,
            mails = contact.takeIfContains(ContactColumn.Mails) { contact.mails }
                ?: currentContact.mails,
            events = contact.takeIfContains(ContactColumn.Events) { contact.events }
                ?: currentContact.events,
            postalAddresses = contact.takeIfContains(ContactColumn.PostalAddresses) { contact.postalAddresses }
                ?: currentContact.postalAddresses,
            note = contact.takeIfContains(ContactColumn.Note) { contact.note } ?: currentContact.note,
            nickname = contact.takeIfContains(ContactColumn.Nickname) { contact.nickname }
                ?: currentContact.nickname,
            groups = contact
                .takeIfContains(ContactColumn.GroupMemberships) { contact.groups }
                ?: currentContact.groups,
            fullNameStyle = contact.takeIfContains(ContactColumn.Names) { contact.fullNameStyle }
                ?: currentContact.fullNameStyle
        )
        val newList = snapshot.value.toMutableList()
            .replace(updatedContact) {
                it.contactId == contact.contactId
            }
            .toList()
        snapshot.emit(newList)
    }

    private fun <T> List<T>.replace(newValue: T, block: (T) -> Boolean): List<T> {
        return map {
            if (block(it)) newValue else it
        }
    }

    override fun fetchContacts(
        predicate: ContactPredicate?,
        columnsToFetch: List<ContactColumn>
    ): Flow<List<Contact>> {
        return snapshot
            .map { contacts ->
                contacts.filter { current ->
                    matchesPredicate(contact = current, predicate)
                }.keepColumns(columnsToFetch)
            }
    }

    private fun matchesPredicate(
        contact: StoredContact,
        predicate: ContactPredicate?
    ): Boolean {
        if (predicate == null) return true
        return when (predicate) {
            is ContactPredicate.ContactLookup -> matchesContact(predicate, contact)
            is ContactPredicate.MailLookup -> {
                val query = predicate.mailAddress.raw
                return contact.mails.any { it.value.raw.startsWith(query, ignoreCase = true) }
            }
            is ContactPredicate.NameLookup -> matchesName(predicate, contact)
            is ContactPredicate.PhoneLookup -> matchesPhone(predicate, contact)
        }
    }

    private fun matchesContact(
        predicate: ContactPredicate.ContactLookup,
        contact: StoredContact
    ): Boolean {
        val isInIds = predicate.inContactIds.orEmpty().contains(contact.contactId)
        val isFavorite = predicate.isFavorite?.let { contact.isStarred == it } ?: true
        return isInIds && isFavorite
    }

    private fun matchesPhone(
        predicate: ContactPredicate.PhoneLookup,
        contact: StoredContact
    ): Boolean {
        val query = predicate.phoneNumber.raw
        return contact.phones.any { it.value.raw.startsWith(query, ignoreCase = true) }
    }

    private fun matchesName(
        predicate: ContactPredicate.NameLookup,
        contact: StoredContact
    ): Boolean {
        val query = predicate.partOfName
        val matchesName = contact.prefix.orEmpty().startsWith(query, ignoreCase = true)
                || contact.firstName.orEmpty().startsWith(query, ignoreCase = true)
                || contact.middleName.orEmpty().startsWith(query, ignoreCase = true)
                || contact.lastName.orEmpty().startsWith(query, ignoreCase = true)
                || contact.suffix.orEmpty().startsWith(query, ignoreCase = true)
        val matchesNick = contact.nickname.orEmpty().startsWith(query, ignoreCase = true)
        return matchesName || matchesNick
    }

    private fun List<StoredContact>.keepColumns(columnsToFetch: List<ContactColumn>): List<Contact> {
        return map {
            PartialContact(
                displayName = displayName(it),
                contactId = it.contactId,
                columns = columnsToFetch,
                isStarred = it.isStarred,

                firstName = valueIfPresent(columnsToFetch, ContactColumn.Names) { it.firstName },
                lastName = valueIfPresent(columnsToFetch, ContactColumn.Names) { it.lastName },
                prefix = valueIfPresent(columnsToFetch, ContactColumn.Names) { it.prefix },
                middleName = valueIfPresent(columnsToFetch, ContactColumn.Names) { it.middleName },
                suffix = valueIfPresent(columnsToFetch, ContactColumn.Names) { it.suffix },
                phoneticFirstName = valueIfPresent(
                    columnsToFetch,
                    ContactColumn.Names
                ) { it.phoneticFirstName },
                phoneticMiddleName = valueIfPresent(
                    columnsToFetch,
                    ContactColumn.Names
                ) { it.phoneticMiddleName },
                phoneticLastName = valueIfPresent(
                    columnsToFetch,
                    ContactColumn.Names
                ) { it.phoneticLastName },
                fullNameStyle = valueIfPresent(
                    columnsToFetch,
                    ContactColumn.Names
                ) { it.fullNameStyle }
                    ?: ContactsContract.FullNameStyle.UNDEFINED,
                phoneticNameStyle = valueIfPresent(
                    columnsToFetch,
                    ContactColumn.Names
                ) { it.phoneticNameStyle }
                    ?: ContactsContract.FullNameStyle.UNDEFINED,
                imageData = valueIfPresent(columnsToFetch, ContactColumn.Image) { it.imageData },
                organization = valueIfPresent(
                    columnsToFetch,
                    ContactColumn.Organization
                ) { it.organization },
                jobTitle = valueIfPresent(
                    columnsToFetch,
                    ContactColumn.Organization
                ) { it.jobTitle },
                webAddresses = valueIfPresent(
                    columnsToFetch,
                    ContactColumn.WebAddresses
                ) { it.webAddresses }.orEmpty(),
                phones = valueIfPresent(
                    columnsToFetch,
                    ContactColumn.Phones
                ) { it.phones }.orEmpty(),
                mails = valueIfPresent(columnsToFetch, ContactColumn.Mails) { it.mails }.orEmpty(),
                events = valueIfPresent(
                    columnsToFetch,
                    ContactColumn.Events
                ) { it.events }.orEmpty(),
                postalAddresses = valueIfPresent(
                    columnsToFetch,
                    ContactColumn.PostalAddresses
                ) { it.postalAddresses }.orEmpty(),
                note = valueIfPresent(columnsToFetch, ContactColumn.Note) { it.note },
                nickname = valueIfPresent(columnsToFetch, ContactColumn.Nickname) { it.nickname },
                groups = valueIfPresent(
                    columnsToFetch,
                    ContactColumn.GroupMemberships
                ) { it.groups }.orEmpty()
            )
        }
    }

    private fun displayName(it: StoredContact): String {
        return with(it) {
            buildString {
                prefix?.let { append(it) }

                if (fullNameStyle == ContactsContract.FullNameStyle.UNDEFINED || fullNameStyle == ContactsContract.FullNameStyle.WESTERN) {
                    firstName?.let { appendWord(it) }
                    middleName?.let { appendWord(it) }
                    lastName?.let { appendWord(it) }
                } else if (fullNameStyle == ContactsContract.FullNameStyle.CHINESE) {
                    lastName?.let { appendWord(it) }
                    middleName?.let { appendWord(it, separator = "") }
                    firstName?.let { appendWord(it, separator = "") }
                } else {
                    lastName?.let { appendWord(it) }
                    middleName?.let { appendWord(it) }
                    firstName?.let { appendWord(it) }
                }

                suffix?.let {
                    appendWord(it, separator = ", ")
                }
            }
        }
    }

    private fun StringBuilder.appendWord(word: String, separator: String = " ") {
        if (word.isNotBlank()) {
            if (isNotEmpty()) append(separator)
            append(word)
        }
    }

    private fun <T> valueIfPresent(
        columns: List<ContactColumn>,
        requiredColumn: ContactColumn,
        value: () -> T?
    ): T? {
        return if (columns.contains(requiredColumn)) {
            value()
        } else {
            null
        }
    }
}