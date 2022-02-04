package com.alexstyl.contactstore

import android.content.ContentProviderOperation
import android.content.ContentProviderOperation.newDelete
import android.content.ContentProviderOperation.newInsert
import android.content.ContentProviderOperation.newUpdate
import android.provider.ContactsContract.Groups

internal class GroupOperationsFactory {
    fun addGroupOperation(group: MutableContactGroup): List<ContentProviderOperation> {
        return listOf(
            newInsert(Groups.CONTENT_URI)
                .withValue(Groups.TITLE, group.title)
                .withValue(Groups.NOTES, group.note)
                .build()
        )
    }

    fun updateGroupOperation(group: MutableContactGroup): List<ContentProviderOperation> {
        return listOf(
            newUpdate(Groups.CONTENT_URI)
                .withValue(Groups.TITLE, group.title)
                .withValue(Groups.NOTES, group.note)
                .withSelection("${Groups._ID} = ${group.groupId}", null)
                .build()
        )
    }

    fun deleteGroupOperation(groupId: Long): List<ContentProviderOperation> {
        return listOf(
            newDelete(Groups.CONTENT_URI)
                .withSelection("${Groups._ID} = $groupId", null)
                .build()
        )
    }
}
