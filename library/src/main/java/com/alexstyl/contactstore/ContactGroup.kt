package com.alexstyl.contactstore

public interface ContactGroup {
    public val groupId: Long
    public val title: String
    public val contactCount: Int
    public val note: String?

    public fun equalsGroup(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ContactGroup) return false

        if (groupId != other.groupId) return false
        if (title != other.title) return false
        if (contactCount != other.contactCount) return false
        if (note != other.note) return false

        return true
    }

    public fun hashCodeGroup(): Int {
        var result = groupId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + contactCount
        result = 31 * result + (note?.hashCode() ?: 0)
        return result
    }
}

public data class MutableContactGroup internal constructor(
    override val groupId: Long,
    override var title: String,
    override val contactCount: Int,
    override var note: String?
) : ContactGroup {

    public constructor() : this(
        groupId = -1L,
        title = "",
        contactCount = 0,
        note = null
    )

    override fun equals(other: Any?): Boolean = equalsGroup(other)
    override fun hashCode(): Int = hashCodeGroup()
}

public data class ImmutableContactGroup(
    override val groupId: Long,
    override val title: String,
    override val contactCount: Int,
    override val note: String?,
) : ContactGroup {
    override fun equals(other: Any?): Boolean = equalsGroup(other)
    override fun hashCode(): Int = hashCodeGroup()
}

/**
 * Creates a copy of the ContactGroup that can have its properties modified.
 *
 * Modifying the properties of the group will not affect the stored group of the device.
 * See [ContactStore] to learn how to persist your changes.
 */
public fun ContactGroup.mutableCopy(): MutableContactGroup {
    return MutableContactGroup(
        groupId = groupId,
        title = title,
        contactCount = contactCount,
        note = note
    )
}

/**
 * Creates a copy of the ContactGroup that can have its properties modified.
 *
 * Modifying the properties of the group will not affect the stored group of the device.
 * See [ContactStore] to learn how to persist your changes.
 */
public fun ContactGroup.mutableCopy(builder: MutableContactGroup.() -> Unit): MutableContactGroup {
    return mutableCopy().apply(builder)
}