package com.alexstyl.contactstore

public data class LabeledValue<T> internal constructor(
    val value: T,
    val label: Label,
    val id: Long?,
) where T : Any {
    public constructor(value: T, label: Label) : this(value, label, null)
}

internal fun <T : Any> LabeledValue<T>.requireId(): Long {
    return requireNotNull(id) {
        "The LabeledValue was expected to have an Id, but it was null"
    }
}