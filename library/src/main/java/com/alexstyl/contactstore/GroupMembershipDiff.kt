package com.alexstyl.contactstore


internal fun valuesAdded(
    old: List<GroupMembership>,
    new: List<GroupMembership>
): List<GroupMembership> {
    return new - old
}

internal fun valuesDeleted(
    old: List<GroupMembership>,
    new: List<GroupMembership>
): List<GroupMembership> {
    return old - new
}
