package com.alexstyl.contactstore

import android.content.ContentProviderOperation
import android.content.ContentProviderOperation.newInsert
import android.provider.ContactsContract.Groups

internal class NewGroupOperationsFactory {
    fun addGroupOperation(group: MutableContactGroup): List<ContentProviderOperation> {
        return listOf(createGroupOperation(group))
    }

    private fun createGroupOperation(group: MutableContactGroup): ContentProviderOperation {
        return newInsert(Groups.CONTENT_URI)
            .withValue(Groups.TITLE, group.title)
            .withValue(Groups.NOTES, group.note)
            .build()
    }
}
