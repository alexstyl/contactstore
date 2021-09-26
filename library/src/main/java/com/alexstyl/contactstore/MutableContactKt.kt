package com.alexstyl.contactstore

import android.provider.ContactsContract.FullNameStyle

internal fun Contact.buildStringFromNames(): String = buildString {
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

internal fun StringBuilder.appendWord(word: String, separator: String = " ") {
    if (word.isNotBlank()) {
        if (isNotEmpty()) append(separator)
        append(word)
    }
}
