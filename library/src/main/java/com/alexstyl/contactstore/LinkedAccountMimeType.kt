package com.alexstyl.contactstore

import android.graphics.drawable.Drawable

data class LinkedAccountMimeType(
    val mimetype: String,
    val icon: Drawable,
    val detailColumn: String,
    val summaryColumn: String,
    val packageName: String
)
