package com.alexstyl.contactstore

import com.alexstyl.contactstore.ContactOperation.Delete
import com.alexstyl.contactstore.ContactOperation.DeleteGroup
import com.alexstyl.contactstore.ContactOperation.Insert
import com.alexstyl.contactstore.ContactOperation.InsertGroup
import com.alexstyl.contactstore.ContactOperation.Update
import com.alexstyl.contactstore.ContactOperation.UpdateGroup

/**
 * A [SaveRequest] is responsible of keeping track of any contact modifications you want to perform.
 *
 * @see [ContactStore.execute]
 */
public class SaveRequest {

    private val _requests = mutableListOf<ContactOperation>()

    public val requests: List<ContactOperation>
        get() = _requests.toList()

    /**
     * Updates an existing contact. The passing [MutableContact] must have a valid contactId.
     *
     * Only the values contained in the contacts' [MutableContact.columns], will be updated.
     * Use [ContactStore.fetchContacts] passing the columns you need to update, in order to get a contact with the respective columns.
     *
     * This is done to prevent accidental value overrides.
     */
    public fun update(mutableContact: MutableContact) {
        _requests.add(Update(mutableContact))
    }

    /**
     * Insert the contact into the specified [InternetAccount].
     */
    public fun insert(mutableContact: MutableContact, intoAccount: InternetAccount? = null) {
        _requests.add(Insert(intoAccount, mutableContact))
    }

    public fun insertGroup(mutableContactGroup: MutableContactGroup) {
        _requests.add(InsertGroup(mutableContactGroup))
    }

    public fun delete(contactId: Long) {
        _requests.add(Delete(contactId))
    }

    public fun deleteGroup(groupId: Long) {
        _requests.add(DeleteGroup(groupId))
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

    public fun updateGroup(mutableContactGroup: MutableContactGroup) {
        _requests.add(UpdateGroup(mutableContactGroup))
    }
}
