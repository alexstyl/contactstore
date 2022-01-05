package com.alexstyl.contactstore.utils

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import com.alexstyl.contactstore.uriFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal fun ContentResolver.runQuery(
    contentUri: Uri,
    projection: Array<String>? = null,
    selection: String? = null,
    selectionArgs: Array<String>? = null,
    sortOrder: String? = null
): Cursor? {
    return query(
        contentUri,
        projection,
        selection,
        selectionArgs,
        sortOrder
    )
}

internal fun ContentResolver.runQueryFlow(
    contentUri: Uri,
    projection: Array<String>? = null,
    selection: String? = null,
    selectionArgs: Array<String>? = null,
    sortOrder: String? = null
): Flow<Cursor?> {
    return uriFlow(contentUri)
        .startImmediately()
        .map {
            query(
                contentUri,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )
        }
}

internal fun valueIn(values: List<Any>): String {
    return values.joinToString(",", prefix = "(", postfix = ")")
}
