package com.alexstyl.contactstore

import android.accounts.Account

public fun SaveRequest.insert(
    intoAccount: Account,
    builder: MutableContactBuilder.() -> Unit,
) {
    insert(
        intoAccount = InternetAccount(intoAccount.name, intoAccount.type),
        builder = builder
    )
}

public fun SaveRequest.insert(
    intoAccount: InternetAccount? = null,
    builder: MutableContactBuilder.() -> Unit,
) {
    val values = MutableContactBuilder().apply(builder)
    insert(
        intoAccount = intoAccount,
        mutableContact = MutableContact().apply {
            isStarred = values.isStarred
            imageData = values.imageData

            prefix = values.prefix
            firstName = values.firstName
            middleName = values.middleName
            lastName = values.lastName
            suffix = values.suffix

            fullNameStyle = values.fullNameStyle
            phoneticFirstName = values.phoneticFirstName
            phoneticLastName = values.phoneticLastName
            phoneticMiddleName = values.phoneticMiddleName

            nickname = values.nickname
            note = values.note?.let { Note(it) }

            jobTitle = values.jobTitle
            organization = values.organization

            phones.addAll(values.phones)
            mails.addAll(values.mails)
            events.addAll(values.events)
            postalAddresses.addAll(values.postalAddresses)
            webAddresses.addAll(values.webAddresses)
            groups.addAll(values.groupMemberships)
            sipAddresses.addAll(values.sipAddresses)
            relations.addAll(values.relations)
            imAddresses.addAll(values.imAddresses)
        })
}

public fun SaveRequest.insertGroup(
    intoAccount: Account,
    builder: MutableContactGroupBuilder.() -> Unit,
) {
    insertGroup(
        intoAccount = InternetAccount(name = intoAccount.name, type = intoAccount.type),
        builder = builder
    )
}

/**
 * Creates a new contact group in the given [InternetAccount]. Passing null will stored the group locally on the device.
 */
public fun SaveRequest.insertGroup(
    intoAccount: InternetAccount? = null,
    builder: MutableContactGroupBuilder.() -> Unit,
) {
    val values = MutableContactGroupBuilder().apply(builder)
    insertGroup(MutableContactGroup().apply {
        note = values.note
        title = values.title
        account = intoAccount
    })
}