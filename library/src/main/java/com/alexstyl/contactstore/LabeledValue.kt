package com.alexstyl.contactstore

public data class LabeledValue<T>(
    val value: T,
    val label: Label,
    val id: Long? = null,
) where T : Any

internal fun <T : Any> LabeledValue<T>.requireId(): Long {
    return requireNotNull(id) {
        "The LabeledValue was expected to have an Id, but it was null"
    }
}