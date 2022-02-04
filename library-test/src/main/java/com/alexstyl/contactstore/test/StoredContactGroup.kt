package com.alexstyl.contactstore.test

public data class StoredContactGroup(
    val groupId: Long,
    val title: String = "",
    val note: String? = null,
    val isDeleted : Boolean = false
)