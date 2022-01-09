package com.alexstyl.contactstore

import android.content.ContentResolver
import android.provider.ContactsContract
import com.alexstyl.contactstore.ContactOperation.Delete
import com.alexstyl.contactstore.ContactOperation.Insert
import com.alexstyl.contactstore.ContactOperation.Update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

internal class AndroidContactStore(
    private val contentResolver: ContentResolver,
    private val newContactOperationsFactory: NewContactOperationsFactory,
    private val existingContactOperationsFactory: ExistingContactOperationsFactory,
    private val contactQueries: ContactQueries
) : ContactStore {
    override suspend fun execute(request: SaveRequest) = withContext(Dispatchers.IO) {
        request.requests.map { operation ->
            when (operation) {
                is Update -> existingContactOperationsFactory.updateOperation(operation.contact)
                is Insert -> newContactOperationsFactory.addContactsOperation(operation.contact)
                is Delete -> existingContactOperationsFactory
                    .deleteContactOperation(operation.contactId)
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
    }
}
