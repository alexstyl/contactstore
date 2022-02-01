package com.alexstyl.contactstore

import android.provider.ContactsContract.FullNameStyle
import android.provider.ContactsContract.PhoneticNameStyle
import com.alexstyl.contactstore.ContactColumn.Mails
import com.alexstyl.contactstore.ContactColumn.Names
import com.alexstyl.contactstore.ContactColumn.Nickname
import com.alexstyl.contactstore.ContactColumn.Organization
import com.alexstyl.contactstore.ContactColumn.Phones

/**
 * Creates a display name of the given contact according to the
 * priority menitoned in [android.provider.ContactsContract.DisplayNameSources]
 */
internal fun MutableContact.displayName(): String {
    return buildString {
        if (containsColumn(Names)) {
            appendWord(buildStringFromNames())
        }

        if (isEmpty() && containsColumn(Names)) {
            appendWord(buildStringFromPhoneticNames())
        }
        if (isEmpty() && containsColumn(Nickname)) {
            append(nickname.orEmpty())
        }
        if (isEmpty() && containsColumn(Organization)) {
            append(organization.orEmpty())
            if (isEmpty()) append(jobTitle.orEmpty())
        }
        if (isEmpty() && containsColumn(Phones)) {
            phones.firstOrNull()?.let { append(it.value.raw) }
        }
        if (isEmpty() && containsColumn(Mails)) {
            mails.firstOrNull()?.let { append(it.value.raw) }
        }
    }
}

internal fun Contact.buildStringFromNames(): String = buildString {
    prefix?.let { append(it) }

    when (fullNameStyle) {
        FullNameStyle.CJK -> {
            lastName?.let { appendWord(it) }
            middleName?.let { appendWord(it) }
            firstName?.let { appendWord(it) }
        }
        FullNameStyle.JAPANESE -> {
            lastName?.let { appendWord(it) }
            middleName?.let { appendWord(it) }
            firstName?.let { appendWord(it) }
        }
        FullNameStyle.CHINESE -> {
            lastName?.let { appendWord(it) }
            middleName?.let { appendWord(it, prefix = None) }
            firstName?.let { appendWord(it, prefix = None) }
        }
        else -> {
            firstName?.let { appendWord(it) }
            middleName?.let { appendWord(it) }
            lastName?.let { appendWord(it) }
        }
    }

    suffix?.let {
        appendWord(it, prefix = ", ")
    }
}

private val None = ""

internal fun Contact.buildStringFromPhoneticNames(): String = buildString {
    when (phoneticNameStyle) {
        PhoneticNameStyle.UNDEFINED -> {
            phoneticFirstName?.let { appendWord(it) }
            phoneticMiddleName?.let { appendWord(it) }
            phoneticLastName?.let { appendWord(it) }
        }
        else -> {
            phoneticFirstName?.let { appendWord(it) }
            phoneticMiddleName?.let { appendWord(it) }
            phoneticLastName?.let { appendWord(it) }
        }
    }
}

internal fun StringBuilder.appendWord(word: String, prefix: String = " ") {
    if (word.isNotBlank()) {
        if (isNotEmpty()) append(prefix)
        append(word)
    }
}
