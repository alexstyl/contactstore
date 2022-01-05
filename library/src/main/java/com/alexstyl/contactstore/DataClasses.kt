package com.alexstyl.contactstore

public data class MailAddress(val raw: String)

public data class Note(val raw: String)

public data class EventDate(val dayOfMonth: Int, val month: Int, val year: Int? = null)

public data class ImageData(val raw: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageData

        if (!raw.contentEquals(other.raw)) return false

        return true
    }

    override fun hashCode(): Int {
        return raw.contentHashCode()
    }
}

public data class PhoneNumber(val raw: String)

public data class WebAddress(val raw: String)

public data class PostalAddress(
    val street: String,
    val poBox: String = "",
    val neighborhood: String = "",
    val city: String = "",
    val region: String = "",
    val postCode: String = "",
    val country: String = "",
)

@Suppress("MagicNumber")
public fun PostalAddress(fullAddress: String): PostalAddress {
    val split = fullAddress.split(",").map { it.trim() }
    return PostalAddress(
        split.getOrElse(0) { "" },
        split.getOrElse(1) { "" },
        split.getOrElse(2) { "" },
        split.getOrElse(3) { "" },
        split.getOrElse(4) { "" },
        split.getOrElse(5) { "" },
        split.getOrElse(6) { "" }
    )
}

public data class GroupMembership(
    val groupId: Long,
    val _id: Long? = null,
)

public data class ImAddress(
    val raw: String,
    val protocol: String,
)

public data class SipAddress(val raw: String)

internal fun GroupMembership.requireId(): Long {
    return requireNotNull(_id)
}

public data class LookupKey(val value: String)

public data class Relation(val name: String)