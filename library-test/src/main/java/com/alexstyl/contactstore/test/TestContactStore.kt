package com.alexstyl.contactstore.test

import android.provider.ContactsContract
import com.alexstyl.contactstore.Contact
import com.alexstyl.contactstore.ContactColumn
import com.alexstyl.contactstore.ContactGroup
import com.alexstyl.contactstore.ContactOperation.*
import com.alexstyl.contactstore.ContactPredicate
import com.alexstyl.contactstore.ContactStore
import com.alexstyl.contactstore.DisplayNameStyle
import com.alexstyl.contactstore.ExperimentalContactStoreApi
import com.alexstyl.contactstore.FetchRequest
import com.alexstyl.contactstore.GroupsPredicate
import com.alexstyl.contactstore.ImmutableContactGroup
import com.alexstyl.contactstore.MutableContact
import com.alexstyl.contactstore.MutableContactGroup
import com.alexstyl.contactstore.PartialContact
import com.alexstyl.contactstore.SaveRequest
import com.alexstyl.contactstore.containsColumn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

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
public class TestContactStore(
    contactsSnapshot: List<StoredContact> = emptyList(),
    contactGroupsSnapshot: List<StoredContactGroup> = emptyList(),
) : ContactStore {

    private val contacts: MutableStateFlow<List<StoredContact>> = MutableStateFlow(contactsSnapshot)
    private val contactGroups: MutableStateFlow<List<StoredContactGroup>> =
        MutableStateFlow(contactGroupsSnapshot)

    override fun execute(builder: SaveRequest.() -> Unit) {
        val saveRequest = SaveRequest().apply(builder)
        runBlocking {
            saveRequest.requests.forEach { operation ->
                when (operation) {
                    is Delete -> deleteContact(withId = operation.contactId)
                    is Insert -> insertContact(operation.contact)
                    is Update -> updateContact(operation.contact)
                    is DeleteGroup -> deleteContactGroup(operation.groupId)
                    is InsertGroup -> insertGroup(operation.group)
                    is UpdateGroup -> updateGroup(operation.group)
                }
            }
        }
    }

    private suspend fun updateGroup(group: MutableContactGroup) {
        val currentGroup = contactGroups.value
            .find { it.groupId == group.groupId } ?: return
        val updatedGroup = currentGroup.copy(
            title = group.title,
            note = group.note
        )
        val newList = contactGroups.value.toMutableList()
            .replace(updatedGroup) {
                it.groupId == group.groupId
            }
            .toList()
        contactGroups.emit(newList)
    }

    private suspend fun deleteContact(withId: Long) {
        contacts.emit(
            contacts.value.dropWhile { it.contactId == withId }
        )
    }

    private suspend fun deleteContactGroup(withId: Long) {
        contactGroups.emit(
            contactGroups.value.dropWhile { it.groupId == withId }
        )
    }

    private suspend fun insertGroup(group: MutableContactGroup) {
        val current = contactGroups.value
        contactGroups.emit(
            current.toMutableList().apply {
                add(
                    StoredContactGroup(
                        groupId = group.groupId,
                        title = group.title,
                        note = group.note
                    )
                )
            }
        )
    }

    private suspend fun insertContact(contact: MutableContact) {
        val current = contacts.value
        contacts.emit(
            current.toMutableList()
                .apply {
                    add(
                        StoredContact(
                            contactId = current.size.toLong(),
                            isStarred = contact.isStarred,
                            lookupKey = contact.lookupKey,
                            prefix = contact.takeIfContains(ContactColumn.Names) { contact.prefix }
                                .orEmpty(),
                            firstName = contact.takeIfContains(ContactColumn.Names) { contact.firstName }
                                .orEmpty(),
                            middleName = contact.takeIfContains(ContactColumn.Names) { contact.middleName }
                                .orEmpty(),
                            lastName = contact.takeIfContains(ContactColumn.Names) { contact.lastName }
                                .orEmpty(),
                            suffix = contact.takeIfContains(ContactColumn.Names) { contact.suffix }
                                .orEmpty(),
                            phoneticMiddleName = contact.takeIfContains(ContactColumn.Names) { contact.phoneticMiddleName }
                                .orEmpty(),
                            phoneticFirstName = contact.takeIfContains(ContactColumn.Names) { contact.phoneticFirstName }
                                .orEmpty(),
                            phoneticLastName = contact.takeIfContains(ContactColumn.Names) { contact.phoneticLastName }
                                .orEmpty(),
                            phoneticNameStyle = contact.takeIfContains(ContactColumn.Names) { contact.phoneticNameStyle }
                                ?: ContactsContract.PhoneticNameStyle.UNDEFINED,
                            imageData = contact.takeIfContains(ContactColumn.Image) { contact.imageData },
                            organization = contact.takeIfContains(ContactColumn.Organization) { contact.organization }
                                .orEmpty(),
                            jobTitle = contact.takeIfContains(ContactColumn.Organization) { contact.jobTitle }
                                .orEmpty(),
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
                            nickname = contact.takeIfContains(ContactColumn.Nickname) { contact.nickname }
                                .orEmpty(),
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
        val currentContact = contacts.value
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
            note = contact.takeIfContains(ContactColumn.Note) { contact.note }
                ?: currentContact.note,
            nickname = contact.takeIfContains(ContactColumn.Nickname) { contact.nickname }
                ?: currentContact.nickname,
            groups = contact
                .takeIfContains(ContactColumn.GroupMemberships) { contact.groups }
                ?: currentContact.groups,
            fullNameStyle = contact.takeIfContains(ContactColumn.Names) { contact.fullNameStyle }
                ?: currentContact.fullNameStyle
        )
        val newList = contacts.value.toMutableList()
            .replace(updatedContact) {
                it.contactId == contact.contactId
            }
            .toList()
        contacts.emit(newList)
    }

    private fun <T> List<T>.replace(newValue: T, block: (T) -> Boolean): List<T> {
        return map {
            if (block(it)) newValue else it
        }
    }

    override fun fetchContacts(
        predicate: ContactPredicate?,
        columnsToFetch: List<ContactColumn>,
        displayNameStyle: DisplayNameStyle
    ): FetchRequest<List<Contact>> {
        return FetchRequest(
            contacts
                .map { contacts ->
                    contacts.filter { current ->
                        matchesPredicate(contact = current, predicate)
                    }.keepColumns(columnsToFetch)
                }
        )
    }

    override fun fetchContactGroups(predicate: GroupsPredicate?): FetchRequest<List<ContactGroup>> {
        val flow = combine(contactGroups.map { groups ->
            groups.filter { group ->
                matchesPredicate(group, predicate)
            }
        }, contacts) { groups, contacts ->
            groups
                .map { group ->
                    ImmutableContactGroup(
                        groupId = group.groupId,
                        title = group.title,
                        contactCount = contacts.count { contact ->
                            contact.groups.any { membership -> membership.groupId == group.groupId }
                        },
                        note = group.note,
                        account = group.internetAccount
                    )
                }
        }
        return FetchRequest(flow)
    }

    private fun matchesPredicate(
        contact: StoredContact,
        predicate: ContactPredicate?
    ): Boolean {
        if (predicate == null) return true
        return when (predicate) {
            is ContactPredicate.ContactIdLookup -> matchesContact(predicate, contact)
            is ContactPredicate.MailLookup -> {
                val query = predicate.mailAddress
                return contact.mails.any { it.value.raw.startsWith(query, ignoreCase = true) }
            }
            is ContactPredicate.ContactLookup -> matchesName(predicate, contact)
            is ContactPredicate.PhoneLookup -> matchesPhone(predicate, contact)
        }
    }

    private fun matchesPredicate(
        group: StoredContactGroup,
        predicate: GroupsPredicate?
    ): Boolean {
        if (predicate == null) return group.isDeleted.not()
        return when (predicate) {
            is GroupsPredicate.GroupLookup -> {
                val passesIdCheck = predicate.inGroupIds == null || predicate.inGroupIds.orEmpty()
                    .contains(group.groupId)
                val passedDeletedCheck = if (predicate.includeDeleted) {
                    true
                } else {
                    group.isDeleted.not()
                }
                passesIdCheck && passedDeletedCheck
            }
        }
    }

    private fun matchesContact(
        predicate: ContactPredicate.ContactIdLookup,
        contact: StoredContact
    ): Boolean {
        return predicate.contactId == contact.contactId
    }

    private fun matchesPhone(
        predicate: ContactPredicate.PhoneLookup,
        contact: StoredContact
    ): Boolean {
        val query = predicate.phoneNumber
        return contact.phones.any { it.value.raw.startsWith(query, ignoreCase = true) }
    }

    private fun matchesName(
        predicate: ContactPredicate.ContactLookup,
        contact: StoredContact
    ): Boolean {
        val query = predicate.query
        val matchesName = contact.prefix.startsWith(query, ignoreCase = true)
                || contact.firstName.startsWith(query, ignoreCase = true)
                || contact.middleName.startsWith(query, ignoreCase = true)
                || contact.lastName.startsWith(query, ignoreCase = true)
                || contact.suffix.startsWith(query, ignoreCase = true)
        val matchesNick = contact.nickname.startsWith(query, ignoreCase = true)
        return matchesName || matchesNick
    }

    private fun List<StoredContact>.keepColumns(columnsToFetch: List<ContactColumn>): List<Contact> {
        return map {
            PartialContact(
                displayName = displayName(it),
                contactId = it.contactId,
                lookupKey = it.lookupKey,
                columns = columnsToFetch,
                isStarred = it.isStarred,

                firstName = valueIfPresent(
                    columnsToFetch,
                    ContactColumn.Names
                ) { it.firstName }.orEmpty(),
                lastName = valueIfPresent(
                    columnsToFetch,
                    ContactColumn.Names
                ) { it.lastName }.orEmpty(),
                prefix = valueIfPresent(
                    columnsToFetch,
                    ContactColumn.Names
                ) { it.prefix }.orEmpty(),
                middleName = valueIfPresent(
                    columnsToFetch,
                    ContactColumn.Names
                ) { it.middleName }.orEmpty(),
                suffix = valueIfPresent(
                    columnsToFetch,
                    ContactColumn.Names
                ) { it.suffix }.orEmpty(),
                phoneticFirstName = valueIfPresent(
                    columnsToFetch,
                    ContactColumn.Names
                ) { it.phoneticFirstName }.orEmpty(),
                phoneticMiddleName = valueIfPresent(
                    columnsToFetch,
                    ContactColumn.Names
                ) { it.phoneticMiddleName }.orEmpty(),
                phoneticLastName = valueIfPresent(
                    columnsToFetch,
                    ContactColumn.Names
                ) { it.phoneticLastName }.orEmpty(),
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
                ) { it.organization }.orEmpty(),
                jobTitle = valueIfPresent(
                    columnsToFetch,
                    ContactColumn.Organization
                ) { it.jobTitle }.orEmpty(),
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
                nickname = valueIfPresent(
                    columnsToFetch,
                    ContactColumn.Nickname
                ) { it.nickname }.orEmpty(),
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
                append(prefix)

                if (fullNameStyle == ContactsContract.FullNameStyle.UNDEFINED || fullNameStyle == ContactsContract.FullNameStyle.WESTERN) {
                    appendWord(firstName)
                    appendWord(middleName)
                    appendWord(lastName)
                } else if (fullNameStyle == ContactsContract.FullNameStyle.CHINESE) {
                    appendWord(lastName)
                    appendWord(middleName, separator = "")
                    appendWord(firstName, separator = "")
                } else {
                    appendWord(lastName)
                    appendWord(middleName)
                    appendWord(firstName)
                }

                appendWord(suffix, separator = ", ")
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