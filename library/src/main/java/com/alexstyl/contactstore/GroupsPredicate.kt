package com.alexstyl.contactstore

public sealed class GroupsPredicate {
    public data class GroupLookup(
        val inGroupIds: List<Long>? = null,
        val includeDeleted: Boolean = false,
    ) : GroupsPredicate()
}