package com.alexstyl.contactstore

import com.alexstyl.contactstore.ContactColumn.Events
import com.alexstyl.contactstore.ContactColumn.Image
import com.alexstyl.contactstore.ContactColumn.Mails
import com.alexstyl.contactstore.ContactColumn.Names
import com.alexstyl.contactstore.ContactColumn.Note
import com.alexstyl.contactstore.ContactColumn.Organization
import com.alexstyl.contactstore.ContactColumn.Phones
import com.alexstyl.contactstore.ContactColumn.PostalAddresses
import com.alexstyl.contactstore.ContactColumn.WebAddresses

@Suppress("ReturnCount")
internal fun Contact.equalContacts(other: Contact?): Boolean {
    if (this === other) return true
    if (other == null) return false

    if (contactId != other.contactId) return false
    if (columns != other.columns) return false
    if (isStarred != other.isStarred) return false
    if (displayName != other.displayName) return false

    if (containsColumn(Names) && firstName != other.firstName) return false
    if (containsColumn(Names) && lastName != other.lastName) return false
    if (containsColumn(Image) && imageData != other.imageData) return false
    if (containsColumn(Phones) && phones != other.phones) return false
    if (containsColumn(Mails) && mails != other.mails) return false
    if (containsColumn(Events) && events != other.events) return false
    if (containsColumn(PostalAddresses) && postalAddresses != other.postalAddresses) return false
    if (containsColumn(Note) && note != other.note) return false
    if (containsColumn(WebAddresses) && webAddresses != other.webAddresses) return false
    if (containsColumn(Organization) && organization != other.organization) return false
    if (containsColumn(Organization) && jobTitle != other.jobTitle) return false

    return true
}

@Suppress("MagicNumber")
internal fun Contact.contactHashCode(): Int {
    var result = contactId.hashCode()
    result = 31 * result + columns.hashCode()
    result = 31 * result + isStarred.hashCode()
    result = 31 * result + displayName.hashCode()
    result = 31 * result + hashIfContains(Names) { (firstName?.hashCode() ?: 0) }
    result = 31 * result + hashIfContains(Names) { (lastName?.hashCode() ?: 0) }
    result = 31 * result + hashIfContains(Image) { (imageData?.hashCode() ?: 0) }
    result = 31 * result + hashIfContains(Phones) { phones.hashCode() }
    result = 31 * result + hashIfContains(Mails) { mails.hashCode() }
    result = 31 * result + hashIfContains(Events) { events.hashCode() }
    result =
        31 * result + hashIfContains(PostalAddresses) { postalAddresses.hashCode() }
    result = 31 * result + hashIfContains(Note) { (note?.hashCode() ?: 0) }
    result = 31 * result + hashIfContains(WebAddresses) { webAddresses.hashCode() }
    result = 31 * result + hashIfContains(Organization) { (organization?.hashCode() ?: 0) }
    result = 31 * result + hashIfContains(Organization) { (jobTitle?.hashCode() ?: 0) }
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
            " firstName=${withValue(Names, value = { firstName })}," +
            " lastName=${withValue(Names, value = { lastName })}," +
            " imageData=${withValue(Image, value = { imageData })}," +
            " organization=${withValue(Organization, value = { organization })}," +
            " jobTitle=${withValue(Organization, value = { jobTitle })}," +
            " phones=${withValue(Phones, value = { phones })}," +
            " mails=${withValue(Mails, value = { mails })}," +
            " events=${withValue(Events, value = { events })}," +
            " postalAddresses=${withValue(PostalAddresses, value = { postalAddresses })}," +
            " note=${withValue(Note, value = { note })}" +
            ")"
}

private fun Contact.withValue(requiredColumn: ContactColumn, value: () -> Any?): String {
    return if (containsColumn(requiredColumn)) {
        value.invoke().toString()
    } else {
        "N/A"
    }
}
