package com.alexstyl.contactstore.test

import com.alexstyl.contactstore.InternetAccount

public data class StoredContactGroup(
    val groupId: Long,
    val title: String = "",
    val note: String? = null,
    val isDeleted: Boolean = false,
    val internetAccount: InternetAccount? = null,
)