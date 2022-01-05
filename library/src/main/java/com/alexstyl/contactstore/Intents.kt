package com.alexstyl.contactstore

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import androidx.annotation.RequiresApi

/**
 * Creates an [Intent.ACTION_SEND] Intent that shares the vCard of a contact
 *
 * @param lookupKey The [LookupKey] of the contact to be shared
 */
public fun shareVCardIntent(lookupKey: LookupKey): Intent {
    val shareUri = Uri.withAppendedPath(
        ContactsContract.Contacts.CONTENT_VCARD_URI,
        lookupKey.value
    )

    return Intent(Intent.ACTION_SEND).apply {
        type = ContactsContract.Contacts.CONTENT_VCARD_TYPE
        putExtra(Intent.EXTRA_STREAM, shareUri)
    }
}

/**
 * Creates an [Intent.ACTION_SEND] Intent that shares the vCard of multiple contacts
 *
 * @param lookupKeys The [LookupKey]s of the contacts to be shared
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public fun shareVCardIntent(lookupKeys: List<LookupKey>): Intent {
    if (lookupKeys.size == 1) return shareVCardIntent(lookupKeys.first())

    val uriListBuilder = buildString {
        lookupKeys.forEach { lookupKey ->
            if (isNotEmpty()) {
                append(':')
            }
            append(lookupKey)
        }
    }
    val uri = ContactsContract.Contacts.CONTENT_MULTI_VCARD_URI.buildUpon()
        .appendEncodedPath(uriListBuilder)
        .build()
    return Intent(Intent.ACTION_SEND).apply {
        type = ContactsContract.Contacts.CONTENT_VCARD_TYPE
        putExtra(Intent.EXTRA_STREAM, uri)
    }
}
