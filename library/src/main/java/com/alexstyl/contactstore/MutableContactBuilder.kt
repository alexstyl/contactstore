package com.alexstyl.contactstore

import android.net.Uri
import android.provider.ContactsContract

public class MutableContactBuilder(
    public var prefix: String = "",
    public var firstName: String = "",
    public var middleName: String = "",
    public var lastName: String = "",
    public var suffix: String = "",
    public var fullNameStyle: Int = ContactsContract.FullNameStyle.UNDEFINED,
    public var phoneticNameStyle: Int = ContactsContract.PhoneticNameStyle.UNDEFINED,
    public var phoneticFirstName: String = "",
    public var phoneticMiddleName: String = "",
    public var phoneticLastName: String = "",
    public var nickname: String = "",
    public var note: String? = null,
    public var jobTitle: String = "",
    public var organization: String = "",
    public var imageData: ImageData? = null,
    public var isStarred: Boolean = false,
) {
    private val _phones: MutableList<LabeledValue<PhoneNumber>> = mutableListOf()
    internal val phones: List<LabeledValue<PhoneNumber>>
        get() = _phones.toList()

    private val _mails: MutableList<LabeledValue<MailAddress>> = mutableListOf()
    internal val mails: List<LabeledValue<MailAddress>>
        get() = _mails.toList()

    private val _events: MutableList<LabeledValue<EventDate>> = mutableListOf()
    internal val events: List<LabeledValue<EventDate>>
        get() = _events.toList()

    private val _postalAddresses: MutableList<LabeledValue<PostalAddress>> = mutableListOf()
    internal val postalAddresses: List<LabeledValue<PostalAddress>>
        get() = _postalAddresses.toList()

    private val _webAddresses: MutableList<LabeledValue<WebAddress>> = mutableListOf()
    internal val webAddresses: List<LabeledValue<WebAddress>>
        get() = _webAddresses.toList()

    private val _groups: MutableList<GroupMembership> = mutableListOf()
    private val _imAddresses: MutableList<LabeledValue<ImAddress>> = mutableListOf()
    internal val imAddresses: List<LabeledValue<ImAddress>>
        get() = _imAddresses.toList()
    internal val groupMemberships: List<GroupMembership>
        get() = _groups.toList()

    private val _sipAddresses: MutableList<LabeledValue<SipAddress>> = mutableListOf()
    internal val sipAddresses: List<LabeledValue<SipAddress>>
        get() = _sipAddresses.toList()

    private val _relations: MutableList<LabeledValue<Relation>> = mutableListOf()
    internal val relations: List<LabeledValue<Relation>>
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

    public fun webAddress(address: Uri, label: Label) {
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
