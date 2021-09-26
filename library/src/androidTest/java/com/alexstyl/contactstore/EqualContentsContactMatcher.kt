package com.alexstyl.contactstore

import com.alexstyl.contactstore.ContactColumn.EVENTS
import com.alexstyl.contactstore.ContactColumn.IMAGE
import com.alexstyl.contactstore.ContactColumn.MAILS
import com.alexstyl.contactstore.ContactColumn.NAMES
import com.alexstyl.contactstore.ContactColumn.NICKNAME
import com.alexstyl.contactstore.ContactColumn.NOTE
import com.alexstyl.contactstore.ContactColumn.ORGANIZATION
import com.alexstyl.contactstore.ContactColumn.PHONES
import com.alexstyl.contactstore.ContactColumn.POSTAL_ADDRESSES
import com.alexstyl.contactstore.ContactColumn.WEB_ADDRESSES
import com.alexstyl.contactstore.ContactColumn.values
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeDiagnosingMatcher

fun equalContents(expected: Contact): Matcher<in Contact> {
    return EqualContentsContactMatcher(expected)
}

class EqualContentsContactMatcher(
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

                if (containsColumn(NAMES)) {
                    putCommaIfNeeded()
                    append(
                        "prefix = $prefix" +
                                ", firstName = $firstName" +
                                ", middleName = $middleName" +
                                ", lastName = $lastName" +
                                ", suffix = $suffix"
                    )
                }
                if (containsColumn(IMAGE)) {
                    putCommaIfNeeded()
                    append("image = $imageData")
                }
                if (containsColumn(PHONES)) {
                    putCommaIfNeeded()
                    append("phones = ${labeledValues(phones)}")
                }
                if (containsColumn(MAILS)) {
                    putCommaIfNeeded()
                    append("mails = ${labeledValues(mails)}")
                }
                if (containsColumn(ORGANIZATION)) {
                    putCommaIfNeeded()
                    append("organization = $organization, jobTitle = $jobTitle")
                }
                if (containsColumn(NICKNAME)) {
                    putCommaIfNeeded()
                    append("nickname = $nickname")
                }
                if (containsColumn(WEB_ADDRESSES)) {
                    putCommaIfNeeded()
                    append("webAddresses = ${labeledValues(webAddresses)}")
                }
                if (containsColumn(EVENTS)) {
                    putCommaIfNeeded()
                    append("events = ${labeledValues(events)}")
                }
                if (containsColumn(POSTAL_ADDRESSES)) {
                    putCommaIfNeeded()
                    append("postalAddresses = ${labeledValues(postalAddresses)}")
                }
                if (containsColumn(NOTE)) {
                    putCommaIfNeeded()
                    append("note = $note")
                }
            }
        }
    }

    private fun labeledValues(list: List<com.alexstyl.contactstore.LabeledValue<*>>): String {
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
                containsDifferentColumns(actual) -> {
                    mismatchDescription.appendText("Columns were ${columns(actual)}")
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
                phonesAreDifferent(actual) -> {
                    mismatchDescription.appendText("phones were ${labeledValues(actual.phones)}")
                    false
                }
                organizationIsDifferent(actual) -> {
                    mismatchDescription.appendText("organization was ${actual.organization}, jobTitle was ${actual.jobTitle}")
                    false
                }
                mailsAreDifferent(actual) -> {
                    mismatchDescription.appendText("mails were ${labeledValues(actual.mails)}")
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
                displayName != expected.displayName -> {
                    mismatchDescription.appendText("display name was '${actual.displayName}'")
                    false
                }
                else -> true
            }
        }
    }

    private fun namesAreDifferent(actual: Contact): Boolean {
        if (expected.containsColumn(NAMES).not()) {
            return false
        }
        return actual.prefix != expected.prefix
                || actual.firstName != expected.firstName
                || actual.middleName != expected.middleName
                || actual.lastName != expected.lastName
                || actual.suffix != expected.suffix
    }

    private fun notesAreDifferent(actual: Contact): Boolean {
        if (expected.containsColumn(NOTE).not()) {
            return false
        }
        return actual.note != expected.note
    }

    private fun containsDifferentColumns(item: Contact): Boolean {
        return columns(expected) != columns(item)
    }

    private fun columns(item: Contact): List<com.alexstyl.contactstore.ContactColumn> {
        return values().toList()
            .filter { item.containsColumn(it) }
    }

    private fun phonesAreDifferent(actual: Contact): Boolean {
        if (expected.containsColumn(PHONES).not()) {
            return false
        }
        return areLabeledValuesDifferentIgnoringId(actual.phones, expected.phones)
    }

    private fun organizationIsDifferent(actual: Contact): Boolean {
        if (expected.containsColumn(ORGANIZATION).not()) {
            return false
        }
        return expected.organization != actual.organization ||
                expected.jobTitle != actual.jobTitle
    }

    private fun mailsAreDifferent(actual: Contact): Boolean {
        if (expected.containsColumn(MAILS).not()) {
            return false
        }
        return areLabeledValuesDifferentIgnoringId(actual.mails, expected.mails)
    }

    private fun eventsAreDifferent(actual: Contact): Boolean {
        if (expected.containsColumn(EVENTS).not()) {
            return false
        }
        return areLabeledValuesDifferentIgnoringId(actual.events, expected.events)
    }

    private fun postalAreDifferent(actual: Contact): Boolean {
        if (expected.containsColumn(POSTAL_ADDRESSES).not()) {
            return false
        }
        return areLabeledValuesDifferentIgnoringId(actual.postalAddresses, expected.postalAddresses)
    }

    private fun webAddressesAreDifferent(actual: Contact): Boolean {
        if (expected.containsColumn(WEB_ADDRESSES).not()) {
            return false
        }
        return areLabeledValuesDifferentIgnoringId(actual.webAddresses, expected.webAddresses)
    }

    private fun <T : Any> areLabeledValuesDifferentIgnoringId(
        one: List<com.alexstyl.contactstore.LabeledValue<T>>,
        other: List<com.alexstyl.contactstore.LabeledValue<T>>
    ): Boolean {
        val map = one.map { it.copy(id = 0) }
        val map1 = other.map { it.copy(id = 0) }
        return map != map1
    }
}
