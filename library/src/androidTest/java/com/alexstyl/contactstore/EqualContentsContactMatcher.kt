package com.alexstyl.contactstore

import com.alexstyl.contactstore.ContactColumn.Events
import com.alexstyl.contactstore.ContactColumn.ImAddresses
import com.alexstyl.contactstore.ContactColumn.Image
import com.alexstyl.contactstore.ContactColumn.Mails
import com.alexstyl.contactstore.ContactColumn.Names
import com.alexstyl.contactstore.ContactColumn.Nickname
import com.alexstyl.contactstore.ContactColumn.Note
import com.alexstyl.contactstore.ContactColumn.Organization
import com.alexstyl.contactstore.ContactColumn.Phones
import com.alexstyl.contactstore.ContactColumn.PostalAddresses
import com.alexstyl.contactstore.ContactColumn.WebAddresses
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

                if (containsColumn(Names)) {
                    putCommaIfNeeded()
                    append(
                        "prefix = $prefix" +
                                ", firstName = $firstName" +
                                ", middleName = $middleName" +
                                ", lastName = $lastName" +
                                ", suffix = $suffix"
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
                if (containsColumn(WebAddresses)) {
                    putCommaIfNeeded()
                    append("webAddresses = ${labeledValues(webAddresses)}")
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
                imAreDifferent(actual) -> {
                    mismatchDescription.appendText("imAddresses were ${labeledValues(actual.imAddresses)}")
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

    private fun imAreDifferent(actual: Contact): Boolean {
        if (expected.containsColumn(ImAddresses).not()) {
            return false
        }
        return areLabeledValuesDifferentIgnoringId(actual.imAddresses, expected.imAddresses)
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

    private fun notesAreDifferent(actual: Contact): Boolean {
        if (expected.containsColumn(Note).not()) {
            return false
        }
        return actual.note != expected.note
    }

    private fun containsDifferentColumns(item: Contact): Boolean {
        return columns(expected) != columns(item)
    }

    private fun columns(item: Contact): List<ContactColumn> {
        return standardColumns()
            .filter { item.containsColumn(it) }
    }

    private fun phonesAreDifferent(actual: Contact): Boolean {
        if (expected.containsColumn(Phones).not()) {
            return false
        }
        return areLabeledValuesDifferentIgnoringId(actual.phones, expected.phones)
    }

    private fun organizationIsDifferent(actual: Contact): Boolean {
        if (expected.containsColumn(Organization).not()) {
            return false
        }
        return expected.organization != actual.organization ||
                expected.jobTitle != actual.jobTitle
    }

    private fun mailsAreDifferent(actual: Contact): Boolean {
        if (expected.containsColumn(Mails).not()) {
            return false
        }
        return areLabeledValuesDifferentIgnoringId(actual.mails, expected.mails)
    }

    private fun eventsAreDifferent(actual: Contact): Boolean {
        if (expected.containsColumn(Events).not()) {
            return false
        }
        return areLabeledValuesDifferentIgnoringId(actual.events, expected.events)
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
