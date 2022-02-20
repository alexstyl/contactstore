package com.alexstyl.contactstore

import android.content.ContentResolver
import android.provider.ContactsContract
import com.alexstyl.contactstore.ContactOperation.Delete
import com.alexstyl.contactstore.ContactOperation.DeleteGroup
import com.alexstyl.contactstore.ContactOperation.Insert
import com.alexstyl.contactstore.ContactOperation.InsertGroup
import com.alexstyl.contactstore.ContactOperation.Update
import com.alexstyl.contactstore.ContactOperation.UpdateGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

internal class AndroidContactStore(
    private val contentResolver: ContentResolver,
    private val newContactOperationsFactory: NewContactOperationsFactory,
    private val contactGroupOperations: GroupOperationsFactory,
    private val existingContactOperationsFactory: ExistingContactOperationsFactory,
    private val contactQueries: ContactQueries,
    private val groupQueries: ContactGroupQueries
) : ContactStore {

    override suspend fun execute(request: SaveRequest.() -> Unit) {
        val apply = SaveRequest().apply(request)
        apply.requests.map { operation ->
            when (operation) {
                is Update -> existingContactOperationsFactory.updateOperation(operation.contact)
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
    ): Flow<List<Contact>> {
        return contactQueries.queryContacts(predicate, columnsToFetch, displayNameStyle)
            .flowOn(Dispatchers.IO)
    }

    override fun fetchContactGroups(
        predicate: GroupsPredicate?
    ): Flow<List<ContactGroup>> {
        return groupQueries.queryGroups(predicate)
            .flowOn(Dispatchers.IO)
    }
}
