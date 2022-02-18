package com.alexstyl.contactstore.test

import com.alexstyl.contactstore.Contact
import com.alexstyl.contactstore.ContactColumn
import com.alexstyl.contactstore.ContactColumn.CustomDataItems
import com.alexstyl.contactstore.ContactColumn.Events
import com.alexstyl.contactstore.ContactColumn.GroupMemberships
import com.alexstyl.contactstore.ContactColumn.ImAddresses
import com.alexstyl.contactstore.ContactColumn.Image
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
import com.alexstyl.contactstore.LabeledValue
import com.alexstyl.contactstore.containsColumn
import com.alexstyl.contactstore.standardColumns
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeDiagnosingMatcher

// copy of library-test's SamePropertiesMatcher in order to not have to extract to a 3rd module
internal fun samePropertiesAs(expected: Contact): Matcher<in Contact> {
    return SamePropertiesMatcher(expected)
}

private class SamePropertiesMatcher(
    private val expected: Contact
) : TypeSafeDiagnosingMatcher<Contact>() {
    override fun describeTo(description: Description) {
        description.appendText("Contact with: ${contentsOf(expected)}")
    }

    private fun contentsOf(contact: Contact): String {
        return with(contact) {
            buildString {
                append("Columns: ${columns(contact)}")
                append(" displayName = ${contact.displayName}")
                append(" isStarred = ${contact.isStarred}")

                if (containsColumn(Names)) {
                    putCommaIfNeeded()
                    append(
                        "prefix = $prefix" +
                                ", firstName = $firstName" +
                                ", middleName = $middleName" +
                                ", lastName = $lastName" +
                                ", suffix = $suffix" +
                                ", phoneticFirstName = $phoneticFirstName" +
                                ", phoneticMiddleName = $phoneticMiddleName" +
                                ", phoneticLastName = $phoneticLastName"
                    )
                }
                if (containsColumn(Image)) {
                    putCommaIfNeeded()
                    append("image = $imageData")
                }
                if (containsColumn(Phones)) {
                    putCommaIfNeeded()
                    append("phones = ${labeledValues(phones)}")
                }
                if (containsColumn(Mails)) {
                    putCommaIfNeeded()
                    append("mails = ${labeledValues(mails)}")
                }
                if (containsColumn(Organization)) {
                    putCommaIfNeeded()
                    append("organization = $organization, jobTitle = $jobTitle")
                }
                if (containsColumn(Nickname)) {
                    putCommaIfNeeded()
                    append("nickname = $nickname")
                }
                if (containsColumn(Relations)) {
                    putCommaIfNeeded()
                    append("relations = ${labeledValues(relations)}")
                }
                if (containsColumn(WebAddresses)) {
                    putCommaIfNeeded()
                    append("webAddresses = ${labeledValues(webAddresses)}")
                }
                if (containsColumn(SipAddresses)) {
                    putCommaIfNeeded()
                    append("sipAddresses = ${labeledValues(sipAddresses)}")
                }
                if (containsColumn(Events)) {
                    putCommaIfNeeded()
                    append("events = ${labeledValues(events)}")
                }
                if (containsColumn(PostalAddresses)) {
                    putCommaIfNeeded()
                    append("postalAddresses = ${labeledValues(postalAddresses)}")
                }
                if (containsColumn(Note)) {
                    putCommaIfNeeded()
                    append("note = $note")
                }
                if (containsColumn(ImAddresses)) {
                    putCommaIfNeeded()
                    append("imAddresses = ${labeledValues(imAddresses)}")
                }
                if (containsColumn(GroupMemberships)) {
                    putCommaIfNeeded()
                    append("groupMemberships = [${groups.joinToString(", ")}]")
                }
            }
        }
    }

    private fun labeledValues(list: List<LabeledValue<*>>): String {
        return list.joinToString(", ", prefix = "[", postfix = "]") { labeledValue ->
            "label = ${labeledValue.label}, value = ${labeledValue.value}"
        }
    }

    private fun StringBuilder.putCommaIfNeeded() {
        if (isEmpty().not()) append(", ")
    }

    override fun matchesSafely(actual: Contact, mismatchDescription: Description): Boolean {
        return with(actual) {
            when {
                containsDifferentImageData(actual) -> {
                    mismatchDescription.appendText("ImageData was ${actual.imageData}")
                    false
                }
                containsDifferentColumns(actual) -> {
                    mismatchDescription.appendText("Columns were ${columns(actual)}")
                    false
                }
                containsDifferentStarred(actual) -> {
                    mismatchDescription.appendText("phones were ${actual.isStarred}")
                    false
                }
                namesAreDifferent(actual) -> {
                    mismatchDescription.appendText(
                        "prefix was '${actual.prefix}'" +
                                ", firstName was '${actual.firstName}'" +
                                ", middleName was '${actual.middleName}'" +
                                ", lastName was '${actual.lastName}'" +
                                ", suffix was '${actual.suffix}'"
                    )
                    false
                }
                phoneticNamesAreDifferent(actual) -> {
                    mismatchDescription.appendText(
                        "phoneticFirstName was '${actual.phoneticFirstName}'" +
                                ", phoneticMiddleName was '${actual.phoneticMiddleName}'" +
                                ", phoneticLastName was '${actual.phoneticLastName}'"
                    )
                    false
                }
                phonesAreDifferent(actual) -> {
                    mismatchDescription.appendText("phones were ${labeledValues(actual.phones)}")
                    false
                }
                mailsAreDifferent(actual) -> {
                    mismatchDescription.appendText("mails were ${labeledValues(actual.mails)}")
                    false
                }
                imAreDifferent(actual) -> {
                    mismatchDescription.appendText("imAddresses were ${labeledValues(actual.imAddresses)}")
                    false
                }
                sipAreDifferent(actual) -> {
                    mismatchDescription.appendText("sipAddresses were ${labeledValues(actual.sipAddresses)}")
                    false
                }
                organizationIsDifferent(actual) -> {
                    mismatchDescription.appendText("organization was ${actual.organization}, jobTitle was ${actual.jobTitle}")
                    false
                }
                eventsAreDifferent(actual) -> {
                    mismatchDescription.appendText("events were ${labeledValues(actual.events)}")
                    false
                }
                postalAreDifferent(actual) -> {
                    mismatchDescription.appendText("postalAddresses were ${labeledValues(actual.postalAddresses)}")
                    false
                }
                webAddressesAreDifferent(actual) -> {
                    mismatchDescription.appendText("webAddresses were ${labeledValues(actual.webAddresses)}")
                    false
                }
                notesAreDifferent(actual) -> {
                    mismatchDescription.appendText("note was ${actual.note}")
                    false
                }
                relationsAreDifferent(actual) -> {
                    mismatchDescription.appendText("relations were ${actual.relations}")
                    false
                }
                linkedAccountsAreDifferent(actual) -> {
                    mismatchDescription.appendText("customDataItems were ${actual.customDataItems}")
                    false
                }
                displayName != expected.displayName -> {
                    mismatchDescription.appendText("display name was '${actual.displayName}'")
                    false
                }
                else -> true
            }
        }
    }

    private fun relationsAreDifferent(actual: Contact): Boolean {
        if (expected.containsColumn(Relations).not()) {
            return false
        }
        return areLabeledValuesDifferentIgnoringId(actual.relations, expected.relations)
    }

    private fun linkedAccountsAreDifferent(actual: Contact): Boolean {
        if (expected.containsColumn(CustomDataItems).not()) {
            return false
        }
        val map = actual.customDataItems.map { it.copy(id = 0) }
        val map1 = expected.customDataItems.map { it.copy(id = 0) }
        return map != map1
    }

    private fun imAreDifferent(actual: Contact): Boolean {
        if (expected.containsColumn(ImAddresses).not()) {
            return false
        }
        return areLabeledValuesDifferentIgnoringId(actual.imAddresses, expected.imAddresses)
    }

    private fun sipAreDifferent(actual: Contact): Boolean {
        if (expected.containsColumn(SipAddresses).not()) {
            return false
        }
        return areLabeledValuesDifferentIgnoringId(actual.sipAddresses, expected.sipAddresses)
    }

    private fun namesAreDifferent(actual: Contact): Boolean {
        if (expected.containsColumn(Names).not()) {
            return false
        }
        return actual.prefix != expected.prefix
                || actual.firstName != expected.firstName
                || actual.middleName != expected.middleName
                || actual.lastName != expected.lastName
                || actual.suffix != expected.suffix
    }

    private fun phoneticNamesAreDifferent(actual: Contact): Boolean {
        if (expected.containsColumn(Names).not()) {
            return false
        }
        return actual.phoneticFirstName != expected.phoneticFirstName
                || actual.phoneticMiddleName != expected.phoneticMiddleName
                || actual.phoneticLastName != expected.phoneticLastName
    }

    private fun notesAreDifferent(contact: Contact): Boolean {
        if (expected.containsColumn(Note).not()) {
            return false
        }
        return contact.note != expected.note
    }

    private fun containsDifferentColumns(contact: Contact): Boolean {
        return columns(expected) != columns(contact)
    }

    private fun containsDifferentStarred(contact: Contact): Boolean {
        return contact.isStarred != expected.isStarred
    }

    private fun columns(contact: Contact): List<ContactColumn> {
        return standardColumns()
            .filter { contact.containsColumn(it) }
    }

    private fun phonesAreDifferent(contact: Contact): Boolean {
        if (expected.containsColumn(Phones).not()) {
            return false
        }
        return areLabeledValuesDifferentIgnoringId(contact.phones, expected.phones)
    }

    private fun organizationIsDifferent(contact: Contact): Boolean {
        if (expected.containsColumn(Organization).not()) {
            return false
        }
        return expected.organization != contact.organization ||
                expected.jobTitle != contact.jobTitle
    }

    private fun mailsAreDifferent(contact: Contact): Boolean {
        if (expected.containsColumn(Mails).not()) {
            return false
        }
        return areLabeledValuesDifferentIgnoringId(contact.mails, expected.mails)
    }

    private fun containsDifferentImageData(contact: Contact): Boolean {
        if (expected.containsColumn(Image).not()) {
            return false
        }
        return contact.imageData != expected.imageData
    }

    private fun eventsAreDifferent(contact: Contact): Boolean {
        if (expected.containsColumn(Events).not()) {
            return false
        }
        return areLabeledValuesDifferentIgnoringId(contact.events, expected.events)
    }

    private fun postalAreDifferent(actual: Contact): Boolean {
        if (expected.containsColumn(PostalAddresses).not()) {
            return false
        }
        return areLabeledValuesDifferentIgnoringId(actual.postalAddresses, expected.postalAddresses)
    }

    private fun webAddressesAreDifferent(actual: Contact): Boolean {
        if (expected.containsColumn(WebAddresses).not()) {
            return false
        }
        return areLabeledValuesDifferentIgnoringId(actual.webAddresses, expected.webAddresses)
    }

    private fun <T : Any> areLabeledValuesDifferentIgnoringId(
        one: List<LabeledValue<T>>,
        other: List<LabeledValue<T>>
    ): Boolean {
        val map = one.map { it.copy(id = 0) }
        val map1 = other.map { it.copy(id = 0) }
        return map != map1
    }
}
