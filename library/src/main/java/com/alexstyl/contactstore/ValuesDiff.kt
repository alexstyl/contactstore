package com.alexstyl.contactstore


internal fun <T : Any> valuesAdded(
    old: List<LabeledValue<T>>,
    new: List<LabeledValue<T>>
): List<LabeledValue<T>> {
    val noIds = new.filter { it.id == null }

    val valuesWithId = new - noIds
    val addedIds = valuesWithId.filterNot { newValue ->
        old.any { it.requireId() == newValue.requireId() }
    }
    return noIds + addedIds
}

internal fun <T : Any> valuesDeleted(
    old: List<LabeledValue<T>>,
    new: List<LabeledValue<T>>
): List<LabeledValue<T>> {
    val removedIds = old.mapNotNull { it.id } - new.mapNotNull { it.id }

    return old
        .filter { it.id != null }
        .filter { removedIds.contains(it.requireId()) }
}


internal fun <T : Any> valuesUpdated(
    old: List<LabeledValue<T>>,
    new: List<LabeledValue<T>>
): List<LabeledValue<T>> {
    return new.filter { newValue ->
        val oldIndex = old.indexOfFirst { it.id == newValue.id }
        oldIndex != -1 && old[oldIndex] != newValue
    }
}
