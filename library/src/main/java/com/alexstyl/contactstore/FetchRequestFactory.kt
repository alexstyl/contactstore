package com.alexstyl.contactstore

internal class FetchRequestFactory(
    private val contactQueries: ContactQueries,
    private val groupQueries: ContactGroupQueries
) {
    fun fetchContactsRequest(
        predicate: ContactPredicate?,
        columnsToFetch: List<ContactColumn>,
        displayNameStyle: DisplayNameStyle
    ): FetchRequest<List<Contact>> {
        return FetchRequest(
            contactQueries.queryContacts(predicate, columnsToFetch, displayNameStyle)
        )
    }

    fun fetchGroupsRequest(predicate: GroupsPredicate?): FetchRequest<List<ContactGroup>> {
        return FetchRequest(
            groupQueries.queryGroups(predicate)
        )
    }
}
