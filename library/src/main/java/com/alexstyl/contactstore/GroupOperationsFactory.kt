package com.alexstyl.contactstore

import android.content.ContentProviderOperation
import android.content.ContentProviderOperation.*
import android.provider.ContactsContract.Groups

internal class GroupOperationsFactory {
    fun addGroupOperation(group: MutableContactGroup): List<ContentProviderOperation> {
        return listOf(
            newInsert(Groups.CONTENT_URI)
                .withValue(Groups.TITLE, group.title)
                .withValue(Groups.NOTES, group.note)
                .withValue(Groups.ACCOUNT_NAME, group.account?.name)
                .withValue(Groups.ACCOUNT_TYPE, group.account?.type)
                .build()
        )
    }

    fun updateGroupOperation(group: MutableContactGroup): List<ContentProviderOperation> {
        return listOf(
            newUpdate(Groups.CONTENT_URI)
                .withValue(Groups.TITLE, group.title)
                .withValue(Groups.NOTES, group.note)
                .withValue(Groups.ACCOUNT_TYPE, group.account?.type)
                .withValue(Groups.ACCOUNT_NAME, group.account?.name)
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
