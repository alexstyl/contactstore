package com.alexstyl.contactstore

internal sealed class ContactOperation {
    data class Insert(val contact: MutableContact) : ContactOperation()
    data class Update(val contact: MutableContact) : ContactOperation()
    data class Delete(val contactId: Long) : ContactOperation()
}