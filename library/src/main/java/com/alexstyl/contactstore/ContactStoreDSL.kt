package com.alexstyl.contactstore

suspend fun ContactStore.execute(request: SaveRequest.() -> Unit) {
    execute(SaveRequest().apply(request))
}

fun SaveRequest.insert(builder: MutableContactBuilder.() -> Unit) {
    val values = MutableContactBuilder().apply(builder)
    insert(MutableContact().apply {
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
