package com.alexstyl.contactstore.utils

import android.database.Cursor

internal fun <T> Cursor?.iterate(mapping: (Cursor) -> T) {
    this?.use {
        if (moveToFirst().not()) {
            emptyList()
        } else {
            val list = mutableListOf<T>()
            do {
                val element = mapping(this)
                list.add(element)
            } while (this.moveToNext())
            list.toList()
        }
    }
}

internal fun <T> Cursor?.mapEachRow(mapping: (Cursor) -> T): List<T> {
    return this?.use {
        if (moveToFirst().not()) {
            emptyList()
        } else {
            val list = mutableListOf<T>()
            do {
                val element = mapping(this)
                list.add(element)
            } while (this.moveToNext())
            list.toList()
        }
    } ?: emptyList()
}


internal operator fun Cursor.get(column: String): String {
    val columnIndex = getColumnIndexOrThrow(column)
    return getString(columnIndex) ?: ""
}
