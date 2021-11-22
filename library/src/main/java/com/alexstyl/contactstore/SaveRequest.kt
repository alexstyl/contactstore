package com.alexstyl.contactstore

import com.alexstyl.contactstore.ContactOperation.Delete
import com.alexstyl.contactstore.ContactOperation.Insert
import com.alexstyl.contactstore.ContactOperation.Update

/**
 * A [SaveRequest] is responsible of keeping track of any contact modifications you want to perform.
 *
 * @see [ContactStore.execute]
 */
class SaveRequest {

    private val _requests = mutableListOf<ContactOperation>()

    val requests: List<ContactOperation>
        get() = _requests.toList()

    /**
     * Updates an existing contact. The passing [MutableContact] must have a valid contactId.
     *
     * Only the values contained in the contacts' [MutableContact.columns], will be updated.
     * Use [ContactStore.fetchContacts] passing the columns you need to update, in order to get a contact with the respective columns.
     *
     * This is done to prevent accidental value overrides.
     */
    fun update(mutableContact: MutableContact) {
        _requests.add(Update(mutableContact))
    }

    fun insert(mutableContact: MutableContact) {
        _requests.add(Insert(mutableContact))
    }

    fun delete(contactId: Long) {
        _requests.add(Delete(contactId))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SaveRequest

        if (_requests != other._requests) return false

        return true
    }

    override fun hashCode(): Int {
        return _requests.hashCode()
    }

    override fun toString(): String {
        return "SaveRequest(_requests=$_requests)"
    }
}
