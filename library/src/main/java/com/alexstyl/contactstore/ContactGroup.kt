package com.alexstyl.contactstore

public interface ContactGroup {
    public val groupId: Long
    public val title: String
    public val contactCount: Int
    public val note: String?
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
}

public data class ImmutableContactGroup(
    override val groupId: Long,
    override val title: String,
    override val contactCount: Int,
    override val note: String?,
) : ContactGroup


public fun ContactGroup.mutableCopy(): MutableContactGroup {
    return MutableContactGroup(
        groupId = groupId,
        title = title,
        contactCount = contactCount,
        note = note
    )
}

public fun ContactGroup.mutableCopy(builder: MutableContactGroup.() -> Unit): MutableContactGroup {
    return mutableCopy().apply(builder)
}