package com.alexstyl.contactstore

import kotlinx.coroutines.flow.Flow

class TestContactStore(
    databaseSnapshot: List<PartialContact> = emptyList()
) : ContactStore {
    override suspend fun execute(request: SaveRequest) {
        TODO("Not yet implemented")
    }

    override fun fetchContacts(
        predicate: ContactPredicate?,
        columnsToFetch: List<ContactColumn>
    ): Flow<List<Contact>> {
        TODO("Not yet implemented")
    }
}