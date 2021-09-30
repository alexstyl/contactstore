package com.alexstyl.contactstore

sealed class ContactPredicate {
    data class PhoneLookup(val phoneNumber: PhoneNumber) : ContactPredicate()
    data class MailLookup(val mailAddress: MailAddress) : ContactPredicate()

    /**
     * Performs a contact lookup by trying to match the given string against various parts of the contact name.
     */
    data class NameLookup(val partOfName: String) : ContactPredicate()
    data class ContactLookup(
        val inContactIds: List<Long>? = null,
        val isFavorite: Boolean? = null,
    ) : ContactPredicate()
}
