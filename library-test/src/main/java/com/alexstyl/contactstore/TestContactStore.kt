package com.alexstyl.contactstore

import android.provider.ContactsContract
import android.provider.ContactsContract.FullNameStyle
import com.alexstyl.contactstore.ContactColumn.EVENTS
import com.alexstyl.contactstore.ContactColumn.GROUP_MEMBERSHIPS
import com.alexstyl.contactstore.ContactColumn.IMAGE
import com.alexstyl.contactstore.ContactColumn.MAILS
import com.alexstyl.contactstore.ContactColumn.NAMES
import com.alexstyl.contactstore.ContactColumn.NICKNAME
import com.alexstyl.contactstore.ContactColumn.NOTE
import com.alexstyl.contactstore.ContactColumn.ORGANIZATION
import com.alexstyl.contactstore.ContactColumn.PHONES
import com.alexstyl.contactstore.ContactColumn.POSTAL_ADDRESSES
import com.alexstyl.contactstore.ContactColumn.WEB_ADDRESSES
import com.alexstyl.contactstore.ContactOperation.Delete
import com.alexstyl.contactstore.ContactOperation.Insert
import com.alexstyl.contactstore.ContactOperation.Update
import com.alexstyl.contactstore.ContactPredicate.ContactLookup
import com.alexstyl.contactstore.ContactPredicate.MailLookup
import com.alexstyl.contactstore.ContactPredicate.NameLookup
import com.alexstyl.contactstore.ContactPredicate.PhoneLookup
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
                is Delete -> deleteContact(withId = operation.contactId)
                is Insert -> insertContact(operation.contact)
                is Update -> updateContact(operation.contact)
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
                            prefix = contact.takeIfContains(NAMES) { contact.prefix },
                            firstName = contact.takeIfContains(NAMES) { contact.firstName },
                            middleName = contact.takeIfContains(NAMES) { contact.middleName },
                            lastName = contact.takeIfContains(NAMES) { contact.lastName },
                            suffix = contact.takeIfContains(NAMES) { contact.suffix },
                            phoneticMiddleName = contact.takeIfContains(NAMES) { contact.phoneticMiddleName },
                            phoneticFirstName = contact.takeIfContains(NAMES) { contact.phoneticFirstName },
                            phoneticLastName = contact.takeIfContains(NAMES) { contact.phoneticLastName },
                            phoneticNameStyle = contact.takeIfContains(NAMES) { contact.phoneticNameStyle }
                                ?: ContactsContract.PhoneticNameStyle.UNDEFINED,
                            imageData = contact.takeIfContains(IMAGE) { contact.imageData },
                            organization = contact.takeIfContains(ORGANIZATION) { contact.organization },
                            jobTitle = contact.takeIfContains(ORGANIZATION) { contact.jobTitle },
                            webAddresses = contact.takeIfContains(WEB_ADDRESSES) { contact.webAddresses }
                                .orEmpty(),
                            phones = contact.takeIfContains(PHONES) { contact.phones }.orEmpty(),
                            mails = contact.takeIfContains(MAILS) { contact.mails }.orEmpty(),
                            events = contact.takeIfContains(EVENTS) { contact.events }.orEmpty(),
                            postalAddresses = contact.takeIfContains(POSTAL_ADDRESSES) { contact.postalAddresses }
                                .orEmpty(),
                            note = contact.takeIfContains(NOTE) { contact.note },
                            nickname = contact.takeIfContains(NICKNAME) { contact.nickname },
                            groups = contact
                                .takeIfContains(GROUP_MEMBERSHIPS) { contact.groups }
                                .orEmpty(),
                            fullNameStyle = contact.takeIfContains(NAMES) { contact.fullNameStyle }
                                ?: FullNameStyle.UNDEFINED
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
            firstName = contact.takeIfContains(NAMES) { contact.firstName }
                ?: currentContact.firstName,
            isStarred = contact.isStarred,
            prefix = contact.takeIfContains(NAMES) { contact.prefix }
                ?: currentContact.prefix,
            middleName = contact.takeIfContains(NAMES) { contact.middleName }
                ?: currentContact.middleName,
            lastName = contact.takeIfContains(NAMES) { contact.lastName }
                ?: currentContact.lastName,
            suffix = contact.takeIfContains(NAMES) { contact.suffix }
                ?: currentContact.suffix,
            phoneticMiddleName = contact.takeIfContains(NAMES) { contact.phoneticMiddleName }
                ?: currentContact.phoneticMiddleName,
            phoneticFirstName = contact.takeIfContains(NAMES) { contact.phoneticFirstName }
                ?: currentContact.phoneticFirstName,
            phoneticLastName = contact.takeIfContains(NAMES) { contact.phoneticLastName }
                ?: currentContact.phoneticLastName,
            phoneticNameStyle = contact.takeIfContains(NAMES) { contact.phoneticNameStyle }
                ?: currentContact.phoneticNameStyle,
            imageData = contact.takeIfContains(IMAGE) { contact.imageData }
                ?: currentContact.imageData,
            organization = contact.takeIfContains(ORGANIZATION) { contact.organization }
                ?: currentContact.organization,
            jobTitle = contact.takeIfContains(ORGANIZATION) { contact.jobTitle }
                ?: currentContact.jobTitle,
            webAddresses = contact.takeIfContains(WEB_ADDRESSES) { contact.webAddresses }
                ?: currentContact.webAddresses,
            phones = contact.takeIfContains(PHONES) { contact.phones }
                ?: currentContact.phones,
            mails = contact.takeIfContains(MAILS) { contact.mails }
                ?: currentContact.mails,
            events = contact.takeIfContains(EVENTS) { contact.events }
                ?: currentContact.events,
            postalAddresses = contact.takeIfContains(POSTAL_ADDRESSES) { contact.postalAddresses }
                ?: currentContact.postalAddresses,
            note = contact.takeIfContains(NOTE) { contact.note } ?: currentContact.note,
            nickname = contact.takeIfContains(NICKNAME) { contact.nickname }
                ?: currentContact.nickname,
            groups = contact
                .takeIfContains(GROUP_MEMBERSHIPS) { contact.groups }
                ?: currentContact.groups,
            fullNameStyle = contact.takeIfContains(NAMES) { contact.fullNameStyle }
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
            is ContactLookup -> matchesContact(predicate, contact)
            is MailLookup -> {
                val query = predicate.mailAddress.raw
                return contact.mails.any { it.value.raw.startsWith(query, ignoreCase = true) }
            }
            is NameLookup -> matchesName(predicate, contact)
            is PhoneLookup -> matchesPhone(predicate, contact)
        }
    }

    private fun matchesContact(
        predicate: ContactLookup,
        contact: StoredContact
    ): Boolean {
        val isInIds = predicate.inContactIds.orEmpty().contains(contact.contactId)
        val isFavorite = predicate.isFavorite?.let { contact.isStarred == it } ?: true
        return isInIds && isFavorite
    }

    private fun matchesPhone(
        predicate: PhoneLookup,
        contact: StoredContact
    ): Boolean {
        val query = predicate.phoneNumber.raw
        return contact.phones.any { it.value.raw.startsWith(query, ignoreCase = true) }
    }

    private fun matchesName(
        predicate: NameLookup,
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

                firstName = valueIfPresent(columnsToFetch, NAMES) { it.firstName },
                lastName = valueIfPresent(columnsToFetch, NAMES) { it.lastName },
                prefix = valueIfPresent(columnsToFetch, NAMES) { it.prefix },
                middleName = valueIfPresent(columnsToFetch, NAMES) { it.middleName },
                suffix = valueIfPresent(columnsToFetch, NAMES) { it.suffix },
                phoneticFirstName = valueIfPresent(
                    columnsToFetch,
                    NAMES
                ) { it.phoneticFirstName },
                phoneticMiddleName = valueIfPresent(
                    columnsToFetch,
                    NAMES
                ) { it.phoneticMiddleName },
                phoneticLastName = valueIfPresent(
                    columnsToFetch,
                    NAMES
                ) { it.phoneticLastName },
                fullNameStyle = valueIfPresent(columnsToFetch, NAMES) { it.fullNameStyle }
                    ?: FullNameStyle.UNDEFINED,
                phoneticNameStyle = valueIfPresent(
                    columnsToFetch,
                    NAMES
                ) { it.phoneticNameStyle }
                    ?: FullNameStyle.UNDEFINED,
                imageData = valueIfPresent(columnsToFetch, IMAGE) { it.imageData },
                organization = valueIfPresent(columnsToFetch, ORGANIZATION) { it.organization },
                jobTitle = valueIfPresent(columnsToFetch, ORGANIZATION) { it.jobTitle },
                webAddresses = valueIfPresent(
                    columnsToFetch,
                    WEB_ADDRESSES
                ) { it.webAddresses }.orEmpty(),
                phones = valueIfPresent(columnsToFetch, PHONES) { it.phones }.orEmpty(),
                mails = valueIfPresent(columnsToFetch, MAILS) { it.mails }.orEmpty(),
                events = valueIfPresent(columnsToFetch, EVENTS) { it.events }.orEmpty(),
                postalAddresses = valueIfPresent(
                    columnsToFetch,
                    POSTAL_ADDRESSES
                ) { it.postalAddresses }.orEmpty(),
                note = valueIfPresent(columnsToFetch, NOTE) { it.note },
                nickname = valueIfPresent(columnsToFetch, NICKNAME) { it.nickname },
                groups = valueIfPresent(
                    columnsToFetch,
                    GROUP_MEMBERSHIPS
                ) { it.groups }.orEmpty()
            )
        }
    }

    private fun displayName(it: StoredContact): String { // TODO copy paste form MutableContact
        return with(it) {
            buildString {
                prefix?.let { append(it) }

                if (fullNameStyle == FullNameStyle.UNDEFINED || fullNameStyle == FullNameStyle.WESTERN) {
                    firstName?.let { appendWord(it) }
                    middleName?.let { appendWord(it) }
                    lastName?.let { appendWord(it) }
                } else if (fullNameStyle == FullNameStyle.CHINESE) {
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
