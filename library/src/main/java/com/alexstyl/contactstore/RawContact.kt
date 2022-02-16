package com.alexstyl.contactstore

import android.content.ContentValues

internal class RawContact(val rawContactContentValues: ContentValues) {
    val dataItems = mutableListOf<ContentValues>()

    fun addDataItemValues(dataItem: ContentValues) {
        dataItems.add(dataItem)
    }
}
