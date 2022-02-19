package com.alexstyl.contactstore

public sealed class ContactPredicate {
    public data class PhoneLookup(val phoneNumber: PhoneNumber) : ContactPredicate()
    public data class MailLookup(val mailAddress: MailAddress) : ContactPredicate()

    /**
     * Performs a contact lookup by trying to match the given string against various parts of the contact name.
     */
    public data class NameLookup(val partOfName: String) : ContactPredicate()

    /**
     * Performs a contact lookup by trying to find a contact with the given contact id.
     */
    public data class ContactLookup(
        val contactId: Long,
        @Deprecated(
            "This property does nothing and will go away in 1.0.0. Perform multiple contactFetches using ContactStore#fetchContacts() using FlowKt.combine()",
            ReplaceWith("contactId")
        )
        val inContactIds: List<Long>? = null,
        @Deprecated("This property is not used. It will go away in 1.0.0")
        val isFavorite: Boolean? = null,
    ) : ContactPredicate()
}
