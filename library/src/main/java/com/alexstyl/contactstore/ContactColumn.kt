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

    companion object {
        internal fun standardColumns(): List<ContactColumn> {
            return listOf(
                Phones,
                Mails,
                Note,
                Events,
                PostalAddresses,
                Image,
                Names,
                Nickname,
                WebAddresses,
                Organization,
                GroupMemberships
            )
        }
    }
}
