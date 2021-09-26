package com.alexstyl.contactstore

import com.alexstyl.contactstore.ContactColumn.EVENTS
import com.alexstyl.contactstore.ContactColumn.IMAGE
import com.alexstyl.contactstore.ContactColumn.MAILS
import com.alexstyl.contactstore.ContactColumn.NAMES
import com.alexstyl.contactstore.ContactColumn.NOTE
import com.alexstyl.contactstore.ContactColumn.ORGANIZATION
import com.alexstyl.contactstore.ContactColumn.PHONES
import com.alexstyl.contactstore.ContactColumn.POSTAL_ADDRESSES
import com.alexstyl.contactstore.ContactColumn.WEB_ADDRESSES

@Suppress("ReturnCount")
internal fun Contact.equalContacts(other: Contact?): Boolean {
    if (this === other) return true
    if (other == null) return false

    if (contactId != other.contactId) return false
    if (columns != other.columns) return false
    if (isStarred != other.isStarred) return false
    if (displayName != other.displayName) return false

    if (containsColumn(NAMES) && firstName != other.firstName) return false
    if (containsColumn(NAMES) && lastName != other.lastName) return false
    if (containsColumn(IMAGE) && imageData != other.imageData) return false
    if (containsColumn(PHONES) && phones != other.phones) return false
    if (containsColumn(MAILS) && mails != other.mails) return false
    if (containsColumn(EVENTS) && events != other.events) return false
    if (containsColumn(POSTAL_ADDRESSES) && postalAddresses != other.postalAddresses) return false
    if (containsColumn(NOTE) && note != other.note) return false
    if (containsColumn(WEB_ADDRESSES) && webAddresses != other.webAddresses) return false
    if (containsColumn(ORGANIZATION) && organization != other.organization) return false
    if (containsColumn(ORGANIZATION) && jobTitle != other.jobTitle) return false

    return true
}

@Suppress("MagicNumber")
internal fun Contact.contactHashCode(): Int {
    var result = contactId.hashCode()
    result = 31 * result + columns.hashCode()
    result = 31 * result + isStarred.hashCode()
    result = 31 * result + displayName.hashCode()
    result = 31 * result + hashIfContains(NAMES) { (firstName?.hashCode() ?: 0) }
    result = 31 * result + hashIfContains(NAMES) { (lastName?.hashCode() ?: 0) }
    result = 31 * result + hashIfContains(IMAGE) { (imageData?.hashCode() ?: 0) }
    result = 31 * result + hashIfContains(PHONES) { phones.hashCode() }
    result = 31 * result + hashIfContains(MAILS) { mails.hashCode() }
    result = 31 * result + hashIfContains(EVENTS) { events.hashCode() }
    result =
        31 * result + hashIfContains(POSTAL_ADDRESSES) { postalAddresses.hashCode() }
    result = 31 * result + hashIfContains(NOTE) { (note?.hashCode() ?: 0) }
    result = 31 * result + hashIfContains(WEB_ADDRESSES) { webAddresses.hashCode() }
    result = 31 * result + hashIfContains(ORGANIZATION) { (organization?.hashCode() ?: 0) }
    result = 31 * result + hashIfContains(ORGANIZATION) { (jobTitle?.hashCode() ?: 0) }
    return result
}

internal fun Contact.hashIfContains(column: ContactColumn, function: () -> Int): Int {
    if (containsColumn(column)) {
        return function.invoke()
    }
    return 0
}

internal fun Contact.toFullString(): String {
    return this.javaClass.simpleName +
            "(contactId=$contactId," +
            " displayName=$displayName," +
            " isStarred=$isStarred," +
            " columns=$columns," +
            " firstName=${withValue(NAMES, value = { firstName })}," +
            " lastName=${withValue(NAMES, value = { lastName })}," +
            " imageData=${withValue(IMAGE, value = { imageData })}," +
            " organization=${withValue(ORGANIZATION, value = { organization })}," +
            " jobTitle=${withValue(ORGANIZATION, value = { jobTitle })}," +
            " phones=${withValue(PHONES, value = { phones })}," +
            " mails=${withValue(MAILS, value = { mails })}," +
            " events=${withValue(EVENTS, value = { events })}," +
            " postalAddresses=${withValue(POSTAL_ADDRESSES, value = { postalAddresses })}," +
            " note=${withValue(NOTE, value = { note })}" +
            ")"
}

private fun Contact.withValue(requiredColumn: ContactColumn, value: () -> Any?): String {
    return if (containsColumn(requiredColumn)) {
        value.invoke().toString()
    } else {
        "N/A"
    }
}
