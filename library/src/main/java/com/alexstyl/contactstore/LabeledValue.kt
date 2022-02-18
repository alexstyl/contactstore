package com.alexstyl.contactstore

import android.accounts.Account

public data class LabeledValue<T> internal constructor(
    val value: T,
    val label: Label,
    val id: Long?,
    /**
     * The account in which the value belongs to.
     *
     * Absence of an Account means that the value is stored locally on the device.
     */
    val accountInfo: Account?
) where T : Any {
    public constructor(value: T, label: Label) : this(
        value = value,
        label = label,
        id = null,
        accountInfo = null
    )
}

internal fun <T : Any> LabeledValue<T>.requireId(): Long {
    return requireNotNull(id) {
        "The LabeledValue was expected to have an Id, but it was null"
    }
}