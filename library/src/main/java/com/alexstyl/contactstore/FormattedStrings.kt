package com.alexstyl.contactstore

import android.os.Build
import android.telephony.PhoneNumberUtils
import java.util.Locale

/**
 * Formats the phone number according to the device selected [Locale][java.util.Locale].
 *
 * Returns the formatted phone number or the original phone number if the phone number is considered
 * invalid in terms of formatting.
 */
public val PhoneNumber.formattedNumber: String
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val country = Locale.getDefault().country
            PhoneNumberUtils.formatNumber(raw, country)
                .orEmpty()
                .ifBlank { raw }
        } else {
            @Suppress("DEPRECATION")
            PhoneNumberUtils.formatNumber(raw)
                .orEmpty()
                .ifBlank { raw }
        }
    }

/**
 * The full postal address returned in a single lined [String], separated by commas.
 * i.e:
 *
 * _94  Byrd Lane, Arch, NM, New Mexico, 88130_
 */
public val PostalAddress.unstructuredPostalAddress: String
    get() {
        return buildString {
            appendIfPresent(street)
            appendIfPresent(poBox)
            appendIfPresent(neighborhood)
            appendIfPresent(city)
            appendIfPresent(region)
            appendIfPresent(postCode)
            appendIfPresent(country)
        }
    }

private fun StringBuilder.appendIfPresent(thing: String) {
    if (thing.isNotEmpty()) {
        if (isNotEmpty()) {
            append(", ")
        }
        append(thing)
    }
}

/**
 * The formatted version of the address, split by new lines.
 *
 * i.e:
 *
 * _94  Byrd Lane,_
 *
 * _Arch,_
 *
 * _NM,_
 *
 * _New Mexico,_
 *
 * _88130_
 */
public val PostalAddress.formattedPostalAddress: String
    get() {
        return buildString {
            appendLineIfPresent(street)
            appendLineIfPresent(poBox)
            appendLineIfPresent(neighborhood)
            appendLineIfPresent(city)
            appendLineIfPresent(region)
            appendLineIfPresent(postCode)
            appendLineIfPresent(country)
        }
    }

private fun StringBuilder.appendLineIfPresent(thing: String) {
    if (thing.isNotEmpty()) {
        if (isNotEmpty()) {
            appendLine(", ")
        }
        append(thing)
    }
}
