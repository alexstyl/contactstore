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
 * priority mentioned in [android.provider.ContactsContract.DisplayNameSources]
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
            append(nickname)
        }
        if (isEmpty() && containsColumn(Organization)) {
            append(organization)
            if (isEmpty()) append(jobTitle)
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
    append(prefix)

    when (fullNameStyle) {
        FullNameStyle.CJK -> {
            appendWord(lastName)
            appendWord(middleName)
            appendWord(firstName)
        }
        FullNameStyle.JAPANESE -> {
            appendWord(lastName)
            appendWord(middleName)
            appendWord(firstName)
        }
        FullNameStyle.CHINESE -> {
            appendWord(lastName)
            appendWord(middleName, prefix = None)
            appendWord(firstName, prefix = None)
        }
        else -> {
            appendWord(firstName)
            appendWord(middleName)
            appendWord(lastName)
        }
    }

    appendWord(suffix, prefix = ", ")
}

private const val None = ""

internal fun Contact.buildStringFromPhoneticNames(): String = buildString {
    when (phoneticNameStyle) {
        PhoneticNameStyle.UNDEFINED -> {
            appendWord(phoneticFirstName)
            appendWord(phoneticMiddleName)
            appendWord(phoneticLastName)
        }
        else -> {
            appendWord(phoneticFirstName)
            appendWord(phoneticMiddleName)
            appendWord(phoneticLastName)
        }
    }
}

internal fun StringBuilder.appendWord(word: String, prefix: String = " ") {
    if (word.isNotBlank()) {
        if (isNotEmpty()) append(prefix)
        append(word)
    }
}
