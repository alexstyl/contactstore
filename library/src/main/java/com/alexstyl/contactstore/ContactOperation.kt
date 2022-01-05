package com.alexstyl.contactstore

public sealed class ContactOperation {
    public data class Insert(val contact: MutableContact) : ContactOperation()
    public data class Update(val contact: MutableContact) : ContactOperation()
    public data class Delete(val contactId: Long) : ContactOperation()
}