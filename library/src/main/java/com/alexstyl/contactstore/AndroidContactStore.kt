package com.alexstyl.contactstore

import android.content.ContentResolver
import android.provider.ContactsContract
import com.alexstyl.contactstore.ContactOperation.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

internal class AndroidContactStore(
    private val contentResolver: ContentResolver,
    private val newContactOperationsFactory: NewContactOperationsFactory,
    private val newGroupOperations: GroupOperationsFactory,
    private val existingContactOperationsFactory: ExistingContactOperationsFactory,
    private val contactQueries: ContactQueries,
    private val groupQueries: ContactGroupQueries
) : ContactStore {

    override suspend fun execute(request: SaveRequest.() -> Unit) {
        executeInternal(SaveRequest().apply(request))
    }

    override suspend fun execute(request: SaveRequest) {
        executeInternal(request)
    }

    private suspend fun executeInternal(request: SaveRequest) = withContext(Dispatchers.IO) {
        request.requests.map { operation ->
            when (operation) {
                is Update -> existingContactOperationsFactory.updateOperation(operation.contact)
                is Insert -> newContactOperationsFactory.addContactsOperation(operation.contact)
                is Delete -> existingContactOperationsFactory
                    .deleteContactOperation(operation.contactId)
                is InsertGroup -> newGroupOperations.addGroupOperation(operation.group)
                is UpdateGroup -> newGroupOperations.updateGroupOperation(operation.group)
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
