package com.alexstyl.contactstore

import android.content.ContentResolver
import android.provider.ContactsContract
import com.alexstyl.contactstore.ContactOperation.Delete
import com.alexstyl.contactstore.ContactOperation.DeleteGroup
import com.alexstyl.contactstore.ContactOperation.Insert
import com.alexstyl.contactstore.ContactOperation.InsertGroup
import com.alexstyl.contactstore.ContactOperation.Update
import com.alexstyl.contactstore.ContactOperation.UpdateGroup
import kotlinx.coroutines.runBlocking

internal class AndroidContactStore(
    private val fetchRequestFactory: FetchRequestFactory,
    private val contentResolver: ContentResolver,
    private val newContactOperationsFactory: NewContactOperationsFactory,
    private val contactGroupOperations: GroupOperationsFactory,
    private val existingContactOperationsFactory: ExistingContactOperationsFactory,
) : ContactStore {

    override fun execute(builder: SaveRequest.() -> Unit) {
        val apply = SaveRequest().apply(builder)
        apply.requests.map { operation ->
            when (operation) {
                is Update -> runBlocking {
                    existingContactOperationsFactory.updateOperation(operation.contact)
                }
                is Insert -> newContactOperationsFactory
                    .addContactsOperation(operation.account, operation.contact)
                is Delete -> existingContactOperationsFactory
                    .deleteContactOperation(operation.contactId)
                is InsertGroup -> contactGroupOperations.addGroupOperation(operation.group)
                is UpdateGroup -> contactGroupOperations.updateGroupOperation(operation.group)
                is DeleteGroup -> contactGroupOperations.deleteGroupOperation(operation.groupId)
            }
        }.forEach { ops ->
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ArrayList(ops))
        }
    }

    override fun fetchContacts(
        predicate: ContactPredicate?,
        columnsToFetch: List<ContactColumn>,
        displayNameStyle: DisplayNameStyle
    ): FetchRequest<List<Contact>> {
        return fetchRequestFactory.fetchContactsRequest(predicate, columnsToFetch, displayNameStyle)
    }

    override fun fetchContactGroups(predicate: GroupsPredicate?): FetchRequest<List<ContactGroup>> {
        return fetchRequestFactory.fetchGroupsRequest(predicate)
    }
}
