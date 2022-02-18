package com.alexstyl.contactstore

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("unused") // used for scoping
internal fun <T> Contact.requireColumn(column: ContactColumn, property: T): RequiredReadColumn<T> {
    return RequiredReadColumn(column, property)
}

internal class RequiredReadColumn<T>(
    private val column: ContactColumn,
    private val value: T
) {
    operator fun getValue(contact: Contact, property: KProperty<*>): T {
        return if (contact.containsColumn(column)) {
            value
        } else {
            error("Tried to get ${property.name}, but the contact was missing the [${column.javaClass.simpleName}] column")
        }
    }
}


@Suppress("unused") // kept for scope
internal fun <T> Contact.readWriteField(
    requiredColumn: ContactColumn,
    value: T
): ReadWriteProperty<Contact, T> {
    return object : ReadWriteProperty<Contact, T> {

        private var mutableValue: T = value

        override fun getValue(thisRef: Contact, property: KProperty<*>): T {
            if (thisRef.containsColumn(requiredColumn)) return mutableValue
            error("Tried to get ${property.name}, but the contact did not contain column ${requiredColumn.javaClass.simpleName}")
        }

        override fun setValue(thisRef: Contact, property: KProperty<*>, value: T) {
            if (thisRef.containsColumn(requiredColumn)) {
                mutableValue = value
            } else {
                error("You can only modify columns that the contact has. $requiredColumn was missing")
            }
        }
    }
}

