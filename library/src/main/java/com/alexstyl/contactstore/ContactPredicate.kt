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
     * Performs a contact lookup by trying to match the given string against various parts of the contact.
     *
     * This can be used for implementing free-text search.
     */
    public data class ContactLookup(val query: String) : ContactPredicate()

    /**
     * Performs a contact lookup by trying to find a contact with the given contact id.
     */
    public data class ContactIdLookup(val contactId: Long) : ContactPredicate()

    public companion object {
        @Deprecated(
            "ContactLookup was renamed to ContactIdLookup",
            ReplaceWith("ContactIdLookup(contactId)")
        )
        @Suppress("FunctionName")
        public fun ContactLookup(contactId: Long): ContactPredicate {
            return ContactIdLookup(contactId)
        }

        @Deprecated(
            "NameLookup was renamed to ContactLookup",
            ReplaceWith("ContactLookup(partOfName)")
        )
        @Suppress("FunctionName")
        public fun NameLookup(partOfName: String): ContactPredicate {
            return ContactLookup(partOfName)
        }
    }
}
