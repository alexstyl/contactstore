package com.alexstyl.contactstore

sealed class ContactPredicate {
    data class PhoneLookup(val phoneNumber: PhoneNumber) : ContactPredicate()
    data class MailLookup(val mailAddress: MailAddress) : ContactPredicate()
    data class ContactLookup(
        val inContactIds: List<Long>? = null,
        val isFavorite: Boolean? = null,
    ) : ContactPredicate()
}
