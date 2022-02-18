package com.alexstyl.contactstore

import android.graphics.drawable.Drawable

@Deprecated(
    "LinkedAccountValues have been replaced with CustomDataItem and will be going away on 1.0.0",
    ReplaceWith("CustomDataItem")
)
public typealias LinkedAccountValue = CustomDataItem

public data class CustomDataItem(
    val id: Long,
    val mimeType: String,
    @Deprecated(
        "Account type is going away in 1.0.0. Use the account property instead",
        ReplaceWith("account.type")
    )
    val accountType: String,
    val summary: String,
    val detail: String,
    val icon: Drawable,
    val account: InternetAccount
)
