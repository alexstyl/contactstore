package com.alexstyl.contactstore

import android.graphics.drawable.Drawable

data class LinkedAccountValue(
    val id: Long,
    val mimeType:String,
    val accountType: String,
    val summary: String,
    val detail: String,
    val icon: Drawable
)
