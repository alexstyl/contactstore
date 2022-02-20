package com.alexstyl.contactstore

public sealed class ContactColumn {
    /**
     * A column that will populate the [Contact.phones] field of all queried contacts when requested.
     */
    public object Phones : ContactColumn()


    /**
     * A column that will populate the [Contact.mails] field of all queried contacts when requested.
     */
    public object Mails : ContactColumn()

    /**
     * A column that will populate the [Contact.note] field of all queried contacts when requested.
     */
    public object Note : ContactColumn()

    /**
     * A column that will populate the [Contact.events] field of all queried contacts when requested.
     *
     */
    public object Events : ContactColumn()

    /**
     * A column that will populate the [Contact.postalAddresses] field of all queried contacts when requested.
     */
    public object PostalAddresses : ContactColumn()

    /**
     * A column that will populate the [Contact.imageData] field of all queried contacts when requested.
     *
     * @see Contact.imageData
     */
    public object Image : ContactColumn()

    /**
     * A column that will populate the following fields of all queried contacts when requested:
     * * [Contact.prefix]
     * * [Contact.firstName]
     * * [Contact.middleName]
     * * [Contact.lastName]
     * * [Contact.suffix]
     * * [Contact.phoneticFirstName]
     * * [Contact.phoneticMiddleName]
     * * [Contact.phoneticLastName]
     *
     *
     */
    public object Names : ContactColumn()

    /**
     * A column that will populate the [Contact.nickname] field of all queried contacts when requested.
     */
    public object Nickname : ContactColumn()

    /**
     * A column that will populate the [Contact.webAddresses] field of all queried contacts when requested.
     */
    public object WebAddresses : ContactColumn()

    /**
     * A column that will populate the [Contact.organization] and [Contact.jobTitle] fields of all queried contacts when requested.
     */
    public object Organization : ContactColumn()

    /**
     * A column that will populate the [Contact.groups] field of all queried contacts when requested.
     */
    public object GroupMemberships : ContactColumn()

    /**
     * A column that will populate the [Contact.customDataItems] field of all queried contacts when requested.
     *
     */
    public object CustomDataItems : ContactColumn()

    /**
     * A column that will populate the [Contact.imAddresses] field of all queried contacts when requested.
     */
    public object ImAddresses : ContactColumn()

    /**
     * A column that will populate the [Contact.sipAddresses] field of all queried contacts when requested.
     */
    public object SipAddresses : ContactColumn()

    /**
     * A column that will populate the [Contact.relations] field of all queried contacts when requested.
     */
    public object Relations : ContactColumn()
}

public fun allContactColumns(): List<ContactColumn> {
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
        ContactColumn.SipAddresses,
        ContactColumn.CustomDataItems,
    )
}
