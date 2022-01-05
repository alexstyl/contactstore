package com.alexstyl.contactstore

public sealed class ContactPredicate {
    public data class PhoneLookup(val phoneNumber: PhoneNumber) : ContactPredicate()
    public data class MailLookup(val mailAddress: MailAddress) : ContactPredicate()

    /**
     * Performs a contact lookup by trying to match the given string against various parts of the contact name.
     */
    public data class NameLookup(val partOfName: String) : ContactPredicate()
    public data class ContactLookup(
        val inContactIds: List<Long>? = null,
        val isFavorite: Boolean? = null,
    ) : ContactPredicate()
}
