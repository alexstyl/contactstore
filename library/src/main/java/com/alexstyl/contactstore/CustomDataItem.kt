package com.alexstyl.contactstore

import android.graphics.drawable.Drawable

public data class CustomDataItem(
    val id: Long,
    val mimeType: String,
    val summary: String,
    val detail: String,
    val icon: Drawable,
    val account: InternetAccount
)
