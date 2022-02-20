package com.alexstyl.contactstore

public sealed class ContactPredicate {
    /**
     * Performs a contact lookup by trying to match the given string against each contact's phone numbers.
     */
    public data class PhoneLookup(val phoneNumber: String) : ContactPredicate()

    /**
     * Performs a contact lookup by trying to match the given string against each contact's mail addresses.
     */
    public data class MailLookup(val mailAddress: String) : ContactPredicate()

    /**
     * Performs a contact lookup by trying to match the given string against various parts of the contact name.
     */
    public data class NameLookup(val partOfName: String) : ContactPredicate()

    /**
     * Performs a contact lookup by trying to find a contact with the given contact id.
     */
    public data class ContactLookup(val contactId: Long) : ContactPredicate()
}
