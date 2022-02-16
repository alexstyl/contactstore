package com.alexstyl.contactstore

import android.accounts.Account

public typealias AccountInfo = Account

public data class LabeledValue<T> internal constructor(
    val value: T,
    val label: Label,
    val id: Long?,
    val accountInfo: AccountInfo? = null
) where T : Any {
    public constructor(value: T, label: Label, accountInfo: AccountInfo? = null) : this(
        value,
        label,
        null,
        accountInfo
    )
}

internal fun <T : Any> LabeledValue<T>.requireId(): Long {
    return requireNotNull(id) {
        "The LabeledValue was expected to have an Id, but it was null"
    }
}