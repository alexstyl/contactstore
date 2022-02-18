package com.alexstyl.contactstore

import android.accounts.Account
import android.graphics.drawable.Drawable

public data class LinkedAccountValue(
    val id: Long,
    val mimeType: String,
    @Deprecated("Use account instead", ReplaceWith("account.type"))
    val accountType: String,
    val summary: String,
    val detail: String,
    val icon: Drawable,
    val account: Account
)
