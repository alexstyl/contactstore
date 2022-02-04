package com.alexstyl.contactstore

public sealed class ContactOperation {
    public data class Insert(val contact: MutableContact) : ContactOperation()
    public data class Update(val contact: MutableContact) : ContactOperation()
    public data class Delete(val contactId: Long) : ContactOperation()
    public data class InsertGroup(val group: MutableContactGroup) : ContactOperation()
    public data class UpdateGroup(val group: MutableContactGroup) : ContactOperation()
}