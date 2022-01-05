package com.alexstyl.contactstore

import android.provider.ContactsContract

public data class MutableContactBuilder(
    var prefix: String = "",
    var firstName: String = "",
    var middleName: String = "",
    var lastName: String = "",
    var suffix: String = "",
    var fullNameStyle: Int = ContactsContract.FullNameStyle.UNDEFINED,
    var phoneticNameStyle: Int = ContactsContract.PhoneticNameStyle.UNDEFINED,
    var phoneticFirstName: String? = null,
    var phoneticMiddleName: String? = null,
    var phoneticLastName: String? = null,
    var nickname: String? = null,
    var note: String? = null,
    var jobTitle: String = "",
    var organization: String = "",
    var imageData: ImageData? = null,
    var isStarred: Boolean = false,
) {
    private val _phones: MutableList<LabeledValue<PhoneNumber>> = mutableListOf()
    val phones: List<LabeledValue<PhoneNumber>>
        get() = _phones.toList()

    private val _mails: MutableList<LabeledValue<MailAddress>> = mutableListOf()
    val mails: List<LabeledValue<MailAddress>>
        get() = _mails.toList()

    private val _events: MutableList<LabeledValue<EventDate>> = mutableListOf()
    val events: List<LabeledValue<EventDate>>
        get() = _events.toList()

    private val _postalAddresses: MutableList<LabeledValue<PostalAddress>> = mutableListOf()
    val postalAddresses: List<LabeledValue<PostalAddress>>
        get() = _postalAddresses.toList()

    private val _webAddresses: MutableList<LabeledValue<WebAddress>> = mutableListOf()
    val webAddresses: List<LabeledValue<WebAddress>>
        get() = _webAddresses.toList()

    private val _groups: MutableList<GroupMembership> = mutableListOf()
    private val _imAddresses: MutableList<LabeledValue<ImAddress>> = mutableListOf()
    val imAddresses: List<LabeledValue<ImAddress>>
        get() = _imAddresses.toList()
    val groupMemberships: List<GroupMembership>
        get() = _groups.toList()

    private val _sipAddresses: MutableList<LabeledValue<SipAddress>> = mutableListOf()
    val sipAddresses: List<LabeledValue<SipAddress>>
        get() = _sipAddresses.toList()

    private val _relations: MutableList<LabeledValue<Relation>> = mutableListOf()
    val relations: List<LabeledValue<Relation>>
        get() = _relations.toList()

    public fun phone(
        number: String,
        label: Label
    ) {
        _phones.add(LabeledValue(PhoneNumber(number), label))
    }

    public fun mail(
        address: String,
        label: Label
    ) {
        _mails.add(LabeledValue(MailAddress(address), label))
    }

    public fun event(dayOfMonth: Int, month: Int, year: Int? = null, label: Label) {
        _events.add(
            LabeledValue(EventDate(dayOfMonth = dayOfMonth, month = month, year = year), label)
        )
    }

    public fun postalAddress(
        street: String,
        poBox: String = "",
        neighborhood: String = "",
        city: String = "",
        region: String = "",
        postCode: String = "",
        country: String = "",
        label: Label
    ) {
        _postalAddresses.add(
            LabeledValue(
                value = PostalAddress(
                    street = street,
                    poBox = poBox,
                    neighborhood = neighborhood,
                    city = city,
                    region = region,
                    postCode = postCode,
                    country = country,
                ),
                label = label
            )
        )
    }

    public fun postalAddress(fullAddress: String, label: Label) {
        _postalAddresses.add(
            LabeledValue(
                value = PostalAddress(fullAddress),
                label = label
            )
        )
    }

    public fun webAddress(address: String, label: Label) {
        _webAddresses.add(LabeledValue(WebAddress(address), label))
    }

    public fun groupMembership(groupId: Long) {
        _groups.add(
            GroupMembership(groupId)
        )
    }

    public fun imAddress(address: String, protocol: String, label: Label) {
        _imAddresses.add(
            LabeledValue(ImAddress(raw = address, protocol = protocol), label)
        )
    }

    public fun relation(name: String, label: Label) {
        _relations.add(LabeledValue(Relation(name = name), label))
    }

    public fun sipAddress(address: String, label: Label) {
        _sipAddresses.add(LabeledValue(SipAddress(address), label))
    }
}
