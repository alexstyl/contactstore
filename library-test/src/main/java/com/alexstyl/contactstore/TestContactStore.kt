package com.alexstyl.contactstore

import android.provider.ContactsContract
import android.provider.ContactsContract.FullNameStyle
import com.alexstyl.contactstore.ContactColumn.Events
import com.alexstyl.contactstore.ContactColumn.GroupMemberships
import com.alexstyl.contactstore.ContactColumn.Image
import com.alexstyl.contactstore.ContactColumn.Mails
import com.alexstyl.contactstore.ContactColumn.Names
import com.alexstyl.contactstore.ContactColumn.Nickname
import com.alexstyl.contactstore.ContactColumn.Note
import com.alexstyl.contactstore.ContactColumn.Organization
import com.alexstyl.contactstore.ContactColumn.Phones
import com.alexstyl.contactstore.ContactColumn.PostalAddresses
import com.alexstyl.contactstore.ContactColumn.WebAddresses
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
                            prefix = contact.takeIfContains(Names) { contact.prefix },
                            firstName = contact.takeIfContains(Names) { contact.firstName },
                            middleName = contact.takeIfContains(Names) { contact.middleName },
                            lastName = contact.takeIfContains(Names) { contact.lastName },
                            suffix = contact.takeIfContains(Names) { contact.suffix },
                            phoneticMiddleName = contact.takeIfContains(Names) { contact.phoneticMiddleName },
                            phoneticFirstName = contact.takeIfContains(Names) { contact.phoneticFirstName },
                            phoneticLastName = contact.takeIfContains(Names) { contact.phoneticLastName },
                            phoneticNameStyle = contact.takeIfContains(Names) { contact.phoneticNameStyle }
                                ?: ContactsContract.PhoneticNameStyle.UNDEFINED,
                            imageData = contact.takeIfContains(Image) { contact.imageData },
                            organization = contact.takeIfContains(Organization) { contact.organization },
                            jobTitle = contact.takeIfContains(Organization) { contact.jobTitle },
                            webAddresses = contact.takeIfContains(WebAddresses) { contact.webAddresses }
                                .orEmpty(),
                            phones = contact.takeIfContains(Phones) { contact.phones }.orEmpty(),
                            mails = contact.takeIfContains(Mails) { contact.mails }.orEmpty(),
                            events = contact.takeIfContains(Events) { contact.events }.orEmpty(),
                            postalAddresses = contact.takeIfContains(PostalAddresses) { contact.postalAddresses }
                                .orEmpty(),
                            note = contact.takeIfContains(Note) { contact.note },
                            nickname = contact.takeIfContains(Nickname) { contact.nickname },
                            groups = contact
                                .takeIfContains(GroupMemberships) { contact.groups }
                                .orEmpty(),
                            fullNameStyle = contact.takeIfContains(Names) { contact.fullNameStyle }
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
            firstName = contact.takeIfContains(Names) { contact.firstName }
                ?: currentContact.firstName,
            isStarred = contact.isStarred,
            prefix = contact.takeIfContains(Names) { contact.prefix }
                ?: currentContact.prefix,
            middleName = contact.takeIfContains(Names) { contact.middleName }
                ?: currentContact.middleName,
            lastName = contact.takeIfContains(Names) { contact.lastName }
                ?: currentContact.lastName,
            suffix = contact.takeIfContains(Names) { contact.suffix }
                ?: currentContact.suffix,
            phoneticMiddleName = contact.takeIfContains(Names) { contact.phoneticMiddleName }
                ?: currentContact.phoneticMiddleName,
            phoneticFirstName = contact.takeIfContains(Names) { contact.phoneticFirstName }
                ?: currentContact.phoneticFirstName,
            phoneticLastName = contact.takeIfContains(Names) { contact.phoneticLastName }
                ?: currentContact.phoneticLastName,
            phoneticNameStyle = contact.takeIfContains(Names) { contact.phoneticNameStyle }
                ?: currentContact.phoneticNameStyle,
            imageData = contact.takeIfContains(Image) { contact.imageData }
                ?: currentContact.imageData,
            organization = contact.takeIfContains(Organization) { contact.organization }
                ?: currentContact.organization,
            jobTitle = contact.takeIfContains(Organization) { contact.jobTitle }
                ?: currentContact.jobTitle,
            webAddresses = contact.takeIfContains(WebAddresses) { contact.webAddresses }
                ?: currentContact.webAddresses,
            phones = contact.takeIfContains(Phones) { contact.phones }
                ?: currentContact.phones,
            mails = contact.takeIfContains(Mails) { contact.mails }
                ?: currentContact.mails,
            events = contact.takeIfContains(Events) { contact.events }
                ?: currentContact.events,
            postalAddresses = contact.takeIfContains(PostalAddresses) { contact.postalAddresses }
                ?: currentContact.postalAddresses,
            note = contact.takeIfContains(Note) { contact.note } ?: currentContact.note,
            nickname = contact.takeIfContains(Nickname) { contact.nickname }
                ?: currentContact.nickname,
            groups = contact
                .takeIfContains(GroupMemberships) { contact.groups }
                ?: currentContact.groups,
            fullNameStyle = contact.takeIfContains(Names) { contact.fullNameStyle }
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

                firstName = valueIfPresent(columnsToFetch, Names) { it.firstName },
                lastName = valueIfPresent(columnsToFetch, Names) { it.lastName },
                prefix = valueIfPresent(columnsToFetch, Names) { it.prefix },
                middleName = valueIfPresent(columnsToFetch, Names) { it.middleName },
                suffix = valueIfPresent(columnsToFetch, Names) { it.suffix },
                phoneticFirstName = valueIfPresent(
                    columnsToFetch,
                    Names
                ) { it.phoneticFirstName },
                phoneticMiddleName = valueIfPresent(
                    columnsToFetch,
                    Names
                ) { it.phoneticMiddleName },
                phoneticLastName = valueIfPresent(
                    columnsToFetch,
                    Names
                ) { it.phoneticLastName },
                fullNameStyle = valueIfPresent(columnsToFetch, Names) { it.fullNameStyle }
                    ?: FullNameStyle.UNDEFINED,
                phoneticNameStyle = valueIfPresent(
                    columnsToFetch,
                    Names
                ) { it.phoneticNameStyle }
                    ?: FullNameStyle.UNDEFINED,
                imageData = valueIfPresent(columnsToFetch, Image) { it.imageData },
                organization = valueIfPresent(columnsToFetch, Organization) { it.organization },
                jobTitle = valueIfPresent(columnsToFetch, Organization) { it.jobTitle },
                webAddresses = valueIfPresent(
                    columnsToFetch,
                    WebAddresses
                ) { it.webAddresses }.orEmpty(),
                phones = valueIfPresent(columnsToFetch, Phones) { it.phones }.orEmpty(),
                mails = valueIfPresent(columnsToFetch, Mails) { it.mails }.orEmpty(),
                events = valueIfPresent(columnsToFetch, Events) { it.events }.orEmpty(),
                postalAddresses = valueIfPresent(
                    columnsToFetch,
                    PostalAddresses
                ) { it.postalAddresses }.orEmpty(),
                note = valueIfPresent(columnsToFetch, Note) { it.note },
                nickname = valueIfPresent(columnsToFetch, Nickname) { it.nickname },
                groups = valueIfPresent(
                    columnsToFetch,
                    GroupMemberships
                ) { it.groups }.orEmpty()
            )
        }
    }

    private fun displayName(it: StoredContact): String {
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
