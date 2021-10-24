package com.alexstyl.contactstore

sealed class ContactColumn {
    object Phones : ContactColumn()
    object Mails : ContactColumn()
    object Note : ContactColumn()
    object Events : ContactColumn()
    object PostalAddresses : ContactColumn()
    object Image : ContactColumn()
    object Names : ContactColumn()
    object Nickname : ContactColumn()
    object WebAddresses : ContactColumn()
    object Organization : ContactColumn()
    object GroupMemberships : ContactColumn()
    data class LinkedAccountColumn(val packageName: String) : ContactColumn()
}

fun standardColumns(): List<ContactColumn> {
    return listOf(
        ContactColumn.Phones,
        ContactColumn.Mails,
        ContactColumn.Note,
        ContactColumn.Events,
        ContactColumn.PostalAddresses,
        ContactColumn.Image,
        ContactColumn.Names,
        ContactColumn.Nickname,
        ContactColumn.WebAddresses,
        ContactColumn.Organization,
        ContactColumn.GroupMemberships
    )
}

