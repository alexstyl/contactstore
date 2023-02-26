package com.alexstyl.contactstore

import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract.Groups
import com.alexstyl.contactstore.utils.get
import com.alexstyl.contactstore.utils.mapEachRow
import com.alexstyl.contactstore.utils.runQueryFlow
import com.alexstyl.contactstore.utils.valueIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class ContactGroupQueries(
    private val contentResolver: ContentResolver,
) {
    fun queryGroups(
        predicate: GroupsPredicate?,
    ): Flow<List<ContactGroup>> {
        return query(predicate)
            .mapEachRow {
                val id = it[Groups._ID].toLong()
                val title = it[Groups.TITLE]
                val contactCount = it[Groups.SUMMARY_COUNT].toInt()
                val note = it[Groups.NOTES]
                val accountType = it[Groups.ACCOUNT_TYPE]
                val accountName = it[Groups.ACCOUNT_NAME]
                ImmutableContactGroup(
                    groupId = id,
                    title = title,
                    contactCount = contactCount,
                    note = note,
                    account = if (accountType.isBlank() && accountName.isBlank()) {
                        null
                    } else {
                        InternetAccount(name = accountName, type = accountType)
                    },
                )
            }
    }

    private fun query(predicate: GroupsPredicate?): Flow<Cursor?> {
        return contentResolver
            .runQueryFlow(
                contentUri = Groups.CONTENT_SUMMARY_URI,
                projection = arrayOf(
                    Groups._ID,
                    Groups.TITLE,
                    Groups.SUMMARY_COUNT,
                    Groups.NOTES,
                    Groups.ACCOUNT_TYPE,
                    Groups.ACCOUNT_NAME,
                ),
                selection = selection(from = predicate)
            )
    }

    private fun selection(from: GroupsPredicate?): String {
        val buildString = buildString {
            val groupLookup = from as? GroupsPredicate.GroupLookup

            val deleted = intOf(groupLookup?.includeDeleted ?: false)
            append("${Groups.DELETED} = $deleted")
            append(" AND ${Groups.GROUP_IS_READ_ONLY} = 0")

            groupLookup?.inGroupIds?.let { groupIds ->
                append(" AND ${Groups._ID} IN ${valueIn(groupIds)}")
            }
        }
        return buildString
    }

    private fun intOf(bool: Boolean): Int {
        return if (bool) 1 else 0
    }
}

private fun <O> Flow<Cursor?>.mapEachRow(mapping: (Cursor) -> O): Flow<List<O>> {
    return map {
        it.mapEachRow(mapping)
    }
}
