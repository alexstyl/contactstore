package com.alexstyl.contactstore

sealed class ContactColumn {
    /**
     * A column that will populate the [Contact.phones] field of all queried contacts when requested.
     */
    object Phones : ContactColumn()

    /**
     * A column that will populate the [Contact.mails] field of all queried contacts when requested.
     */
    object Mails : ContactColumn()

    /**
     * A column that will populate the [Contact.note] field of all queried contacts when requested.
     */
    object Note : ContactColumn()

    /**
     * A column that will populate the [Contact.events] field of all queried contacts when requested.
     *
     */
    object Events : ContactColumn()

    /**
     * A column that will populate the [Contact.postalAddresses] field of all queried contacts when requested.
     */
    object PostalAddresses : ContactColumn()

    /**
     * A column that will populate the [Contact.imageData] field of all queried contacts when requested.
     *
     * @see Contact.imageData
     */
    object Image : ContactColumn()

    /**
     * A column that will populate the [Contact.prefix], [Contact.firstName],[Contact.middleName],[Contact.lastName], [Contact.suffix] fields of all queried contacts when requested.
     */
    object Names : ContactColumn()

    /**
     * A column that will populate the [Contact.nickname] field of all queried contacts when requested.
     */
    object Nickname : ContactColumn()

    /**
     * A column that will populate the [Contact.webAddresses] field of all queried contacts when requested.
     */
    object WebAddresses : ContactColumn()

    /**
     * A column that will populate the [Contact.organization] and [Contact.jobTitle] fields of all queried contacts when requested.
     */
    object Organization : ContactColumn()

    /**
     * A column that will populate the [Contact.groups] field of all queried contacts when requested.
     */
    object GroupMemberships : ContactColumn()

    /**
     * A column that will populate the [Contact.linkedAccountValues] field of all queried contacts when requested.
     *
     * Each 3rd party app specifies a unique account type when syncing web contacts into the device.
     * See [SyncColumns.ACCOUNT_TYPE][android.provider.ContactsContract.SyncColumns.ACCOUNT_TYPE] for more details.
     */
    data class LinkedAccountValues(val accountType: String) : ContactColumn()

    /**
     * A column that will populate the [Contact.imAddresses] field of all queried contacts when requested.
     */
    object ImAddresses : ContactColumn()

    /**
     * A column that will populate the [Contact.relations] field of all queried contacts when requested.
     */
    object Relations : ContactColumn()
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
        ContactColumn.GroupMemberships,
        ContactColumn.ImAddresses,
        ContactColumn.Relations,
    )
}
