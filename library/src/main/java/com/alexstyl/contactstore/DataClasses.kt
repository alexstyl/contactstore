package com.alexstyl.contactstore

data class MailAddress(val raw: String)

data class Note(val raw: String)

data class EventDate(val dayOfMonth: Int, val month: Int, val year: Int? = null)

data class ImageData(val raw: ByteArray) {
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

data class PhoneNumber(val raw: String)

data class WebAddress(val raw: String)

data class PostalAddress(
    val street: String,
    val poBox: String = "",
    val neighborhood: String = "",
    val city: String = "",
    val region: String = "",
    val postCode: String = "",
    val country: String = "",
)

@Suppress("MagicNumber")
fun PostalAddress(fullAddress: String): PostalAddress {
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

data class GroupMembership(
    val groupId: Long,
    val _id: Long? = null,
)

data class ImAddress(
    val raw: String,
    val protocol: String,
)

internal fun GroupMembership.requireId(): Long {
    return requireNotNull(_id)
}

data class LookupKey(val value: String)

data class Relation(val name: String)