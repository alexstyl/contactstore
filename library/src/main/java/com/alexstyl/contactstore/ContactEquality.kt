package com.alexstyl.contactstore

import com.alexstyl.contactstore.ContactColumn.Events
import com.alexstyl.contactstore.ContactColumn.GroupMemberships
import com.alexstyl.contactstore.ContactColumn.ImAddresses
import com.alexstyl.contactstore.ContactColumn.Image
import com.alexstyl.contactstore.ContactColumn.LinkedAccountValues
import com.alexstyl.contactstore.ContactColumn.Mails
import com.alexstyl.contactstore.ContactColumn.Names
import com.alexstyl.contactstore.ContactColumn.Nickname
import com.alexstyl.contactstore.ContactColumn.Note
import com.alexstyl.contactstore.ContactColumn.Organization
import com.alexstyl.contactstore.ContactColumn.Phones
import com.alexstyl.contactstore.ContactColumn.PostalAddresses
import com.alexstyl.contactstore.ContactColumn.Relations
import com.alexstyl.contactstore.ContactColumn.SipAddresses
import com.alexstyl.contactstore.ContactColumn.WebAddresses

@Suppress("ReturnCount")
internal fun Contact.equalContacts(other: Contact?): Boolean {
    if (this === other) return true
    if (other == null) return false

    if (contactId != other.contactId) return false
    if (lookupKey != other.lookupKey) return false
    if (isStarred != other.isStarred) return false
    if (columns != other.columns) return false

    if (containsColumn(Names) && prefix != other.prefix) return false
    if (containsColumn(Names) && firstName != other.firstName) return false
    if (containsColumn(Names) && middleName != other.middleName) return false
    if (containsColumn(Names) && lastName != other.lastName) return false
    if (containsColumn(Names) && suffix != other.suffix) return false
    if (containsColumn(Names) && phoneticFirstName != other.phoneticFirstName) return false
    if (containsColumn(Names) && phoneticMiddleName != other.phoneticMiddleName) return false
    if (containsColumn(Names) && phoneticLastName != other.phoneticLastName) return false
    if (containsColumn(Names) && fullNameStyle != other.fullNameStyle) return false
    if (containsColumn(Names) && phoneticNameStyle != other.phoneticNameStyle) return false
    if (containsColumn(Nickname) && nickname != other.nickname) return false

    if (containsColumn(Image) && imageData != other.imageData) return false
    if (containsColumn(Phones) && phones != other.phones) return false
    if (containsColumn(Mails) && mails != other.mails) return false
    if (containsColumn(Events) && events != other.events) return false
    if (containsColumn(PostalAddresses) && postalAddresses != other.postalAddresses) return false
    if (containsColumn(Note) && note != other.note) return false
    if (containsColumn(WebAddresses) && webAddresses != other.webAddresses) return false
    if (containsColumn(SipAddresses) && sipAddresses != other.sipAddresses) return false
    if (containsColumn(ImAddresses) && imAddresses != other.imAddresses) return false
    if (containsColumn(Relations) && relations != other.relations) return false
    if (containsColumn(Organization) && organization != other.organization) return false
    if (containsColumn(Organization) && jobTitle != other.jobTitle) return false
    if (containsColumn(GroupMemberships) && groups != other.groups) return false
    if (columns.any { it is LinkedAccountValues } && linkedAccountValues != other.linkedAccountValues) {
        return false
    }
    if (displayName != other.displayName) return false
    return true
}

@Suppress("MagicNumber")
internal fun Contact.contactHashCode(): Int {
    var result = contactId.hashCode()
    result = 31 * result + (lookupKey?.hashCode() ?: 0)
    result = 31 * result + isStarred.hashCode()
    result = 31 * result + columns.hashCode()
    result = 31 * result + hashIfContains(Image) { imageData?.hashCode() }
    result = 31 * result + hashIfContains(Phones) { phones.hashCode() }
    result = 31 * result + hashIfContains(Mails) { mails.hashCode() }
    result = 31 * result + hashIfContains(Events) { events.hashCode() }
    result = 31 * result + hashIfContains(PostalAddresses) { postalAddresses.hashCode() }
    result = 31 * result + hashIfContains(WebAddresses) { webAddresses.hashCode() }
    result = 31 * result + hashIfContains(ImAddresses) { imAddresses.hashCode() }
    result = 31 * result + hashIfContains(SipAddresses) { sipAddresses.hashCode() }
    result = 31 * result + hashIfContainsLinked { linkedAccountValues.hashCode() }
    result = 31 * result + hashIfContains(Note) { note?.hashCode() }
    result = 31 * result + hashIfContains(GroupMemberships) { groups.hashCode() }
    result = 31 * result + hashIfContains(Relations) { relations.hashCode() }
    result = 31 * result + hashIfContains(Organization) { organization?.hashCode() }
    result = 31 * result + hashIfContains(Organization) { jobTitle?.hashCode() }
    result = 31 * result + hashIfContains(Names) { firstName?.hashCode() }
    result = 31 * result + hashIfContains(Names) { lastName?.hashCode() }
    result = 31 * result + hashIfContains(Names) { middleName?.hashCode() }
    result = 31 * result + hashIfContains(Names) { prefix?.hashCode() }
    result = 31 * result + hashIfContains(Names) { suffix?.hashCode() }
    result = 31 * result + hashIfContains(Names) { phoneticLastName?.hashCode() }
    result = 31 * result + hashIfContains(Names) { phoneticFirstName?.hashCode() }
    result = 31 * result + hashIfContains(Names) { phoneticMiddleName?.hashCode() }
    result = 31 * result + hashIfContains(Names) { fullNameStyle.hashCode() }
    result = 31 * result + hashIfContains(Names) { phoneticNameStyle.hashCode() }
    result = 31 * result + hashIfContains(Names) { nickname?.hashCode() }
    result = 31 * result + displayName.hashCode()

    return result
}

internal fun Contact.hashIfContains(column: ContactColumn, function: () -> Int?): Int {
    if (containsColumn(column)) {
        return function.invoke() ?: 0
    }
    return 0
}

internal fun Contact.hashIfContainsLinked(function: () -> Int?): Int {
    if (columns.any { it is LinkedAccountValues }) {
        return function.invoke() ?: 0
    }
    return 0
}

internal fun Contact.toFullString(): String {
    return "${this.javaClass.simpleName}(contactId=$contactId," +
            " displayName='$displayName'" +
            " lookupKey=$lookupKey," +
            " isStarred=$isStarred," +
            " columns=$columns," +
            " imageData=${withValue(Image) { imageData }}," +
            " phones=${withValue(Phones) { phones }}," +
            " mails=${withValue(Mails) { mails }}," +
            " events=${withValue(Events) { events }}," +
            " postalAddresses=${withValue(PostalAddresses) { postalAddresses }}," +
            " webAddresses=${withValue(WebAddresses) { webAddresses }}," +
            " imAddresses=${withValue(ImAddresses) { imAddresses }}," +
            " sipAddresses=${withValue(SipAddresses) { sipAddresses }}," +
            " linkedAccountValues=${withLinkedValue { linkedAccountValues }}," +
            " note=${withValue(Note) { note }}," +
            " groups=${withValue(GroupMemberships) { groups }}," +
            " relations=${withValue(Relations) { relations }}," +
            " organization=${withValue(Organization) { organization }}," +
            " jobTitle=${withValue(Organization) { jobTitle }}," +
            " firstName=${withValue(Names) { firstName }}," +
            " lastName=${withValue(Names) { lastName }}," +
            " middleName=${withValue(Names) { middleName }}," +
            " prefix=${withValue(Names) { prefix }}," +
            " suffix=${withValue(Names) { suffix }}," +
            " phoneticLastName=${withValue(Names) { phoneticLastName }}," +
            " phoneticFirstName=${withValue(Names) { phoneticFirstName }}," +
            " phoneticMiddleName=${withValue(Names) { phoneticMiddleName }}," +
            " fullNameStyle=${withValue(Names) { fullNameStyle }}," +
            " phoneticNameStyle=${withValue(Names) { phoneticNameStyle }}," +
            " nickname=${withValue(Nickname) { nickname }})"
}

private fun Contact.withValue(requiredColumn: ContactColumn, value: () -> Any?): String {
    return if (containsColumn(requiredColumn)) {
        value.invoke().toString()
    } else {
        "N/A"
    }
}

private fun Contact.withLinkedValue(value: () -> Any?): String {
    return if (columns.any { it is LinkedAccountValues }) {
        value.invoke().toString()
    } else {
        "N/A"
    }
}
