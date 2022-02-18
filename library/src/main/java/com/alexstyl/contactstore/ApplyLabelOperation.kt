package com.alexstyl.contactstore

import android.content.ContentProviderOperation
import android.content.res.Resources
import android.provider.ContactsContract.CommonDataKinds
import android.provider.ContactsContract.CommonDataKinds.Contactables
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.CommonDataKinds.Event
import android.provider.ContactsContract.CommonDataKinds.Im
import android.provider.ContactsContract.CommonDataKinds.Organization
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.Relation
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal
import android.provider.ContactsContract.CommonDataKinds.Website

internal fun ContentProviderOperation.Builder.withPhoneLabel(
    label: Label, resources: Resources
): ContentProviderOperation.Builder {
    return when (label) {
        Label.PhoneNumberMobile -> withValue(Contactables.TYPE, Phone.TYPE_MOBILE)
        Label.LocationHome -> withValue(Contactables.TYPE, Phone.TYPE_HOME)
        Label.LocationWork -> withValue(Contactables.TYPE, Phone.TYPE_WORK)
        Label.PhoneNumberPager -> withValue(Contactables.TYPE, Phone.TYPE_PAGER)
        Label.PhoneNumberCar -> withValue(Contactables.TYPE, Phone.TYPE_CAR)
        Label.PhoneNumberFaxWork -> withValue(Contactables.TYPE, Phone.TYPE_FAX_WORK)
        Label.PhoneNumberFaxHome -> withValue(Contactables.TYPE, Phone.TYPE_FAX_HOME)
        Label.PhoneNumberCallback -> withValue(Contactables.TYPE, Phone.TYPE_CALLBACK)
        Label.PhoneNumberCompanyMain -> withValue(Contactables.TYPE, Phone.TYPE_COMPANY_MAIN)
        Label.PhoneNumberIsdn -> withValue(Contactables.TYPE, Phone.TYPE_ISDN)
        Label.Main -> withValue(Contactables.TYPE, Phone.TYPE_MAIN)
        Label.PhoneNumberOtherFax -> withValue(Contactables.TYPE, Phone.TYPE_OTHER_FAX)
        Label.PhoneNumberRadio -> withValue(Contactables.TYPE, Phone.TYPE_RADIO)
        Label.PhoneNumberTelex -> withValue(Contactables.TYPE, Phone.TYPE_TELEX)
        Label.PhoneNumberTtyTdd -> withValue(Contactables.TYPE, Phone.TYPE_TTY_TDD)
        Label.PhoneNumberWorkPager -> withValue(Contactables.TYPE, Phone.TYPE_WORK_PAGER)
        Label.PhoneNumberWorkMobile -> withValue(Contactables.TYPE, Phone.TYPE_WORK_MOBILE)
        Label.PhoneNumberAssistant -> withValue(Contactables.TYPE, Phone.TYPE_ASSISTANT)
        Label.PhoneNumberMms -> withValue(Contactables.TYPE, Phone.TYPE_MMS)
        Label.Other -> withValue(Contactables.TYPE, Phone.TYPE_OTHER)
        else -> withCustomLabel(resources, label)
    }
}

internal fun ContentProviderOperation.Builder.withMailLabel(
    label: Label, resources: Resources
): ContentProviderOperation.Builder {
    return when (label) {
        Label.LocationHome -> withValue(Contactables.TYPE, Email.TYPE_HOME)
        Label.LocationWork -> withValue(Contactables.TYPE, Email.TYPE_WORK)
        Label.Other -> withValue(Contactables.TYPE, Email.TYPE_OTHER)
        Label.PhoneNumberMobile -> withValue(Contactables.TYPE, Email.TYPE_MOBILE)
        is Label.Custom -> withValue(Contactables.TYPE, Contactables.TYPE_CUSTOM)
            .withValue(Contactables.LABEL, label.label)
        else -> withCustomLabel(resources, label)
    }
}

internal fun ContentProviderOperation.Builder.withRelationLabel(
    label: Label, resources: Resources
): ContentProviderOperation.Builder {
    return when (label) {
        Label.PhoneNumberAssistant -> withValue(Contactables.TYPE, Relation.TYPE_ASSISTANT)
        Label.RelationBrother -> withValue(Contactables.TYPE, Relation.TYPE_BROTHER)
        Label.RelationDomesticPartner -> withValue(
            Contactables.TYPE, Relation.TYPE_DOMESTIC_PARTNER
        )
        Label.RelationChild -> withValue(Contactables.TYPE, Relation.TYPE_CHILD)
        Label.RelationFather -> withValue(Contactables.TYPE, Relation.TYPE_FATHER)
        Label.RelationMother -> withValue(Contactables.TYPE, Relation.TYPE_MOTHER)
        Label.RelationManager -> withValue(Contactables.TYPE, Relation.TYPE_MANAGER)
        Label.RelationFriend -> withValue(Contactables.TYPE, Relation.TYPE_FRIEND)
        Label.RelationParent -> withValue(Contactables.TYPE, Relation.TYPE_PARENT)
        Label.RelationPartner -> withValue(Contactables.TYPE, Relation.TYPE_PARTNER)
        Label.RelationReferredBy -> withValue(Contactables.TYPE, Relation.TYPE_REFERRED_BY)
        Label.RelationSister -> withValue(Contactables.TYPE, Relation.TYPE_SISTER)
        Label.RelationSpouse -> withValue(Contactables.TYPE, Relation.TYPE_SPOUSE)
        Label.RelationRelative -> withValue(Contactables.TYPE, Relation.TYPE_RELATIVE)
        Label.Other -> withValue(Contactables.TYPE, Relation.TYPE_CHILD)
        is Label.Custom -> withValue(
            Contactables.TYPE,
            Contactables.TYPE_CUSTOM
        )
            .withValue(Contactables.LABEL, label.label)
        else -> withCustomLabel(resources, label)
    }
}

internal fun ContentProviderOperation.Builder.withSipLabel(
    label: Label, resources: Resources
): ContentProviderOperation.Builder {
    return when (label) {
        Label.LocationHome -> withValue(Contactables.TYPE, CommonDataKinds.SipAddress.TYPE_HOME)
        Label.Other -> withValue(Contactables.TYPE, CommonDataKinds.SipAddress.TYPE_OTHER)
        Label.LocationWork -> withValue(Contactables.TYPE, CommonDataKinds.SipAddress.TYPE_WORK)
        is Label.Custom -> withValue(
            Contactables.TYPE,
            Contactables.TYPE_CUSTOM
        )
            .withValue(Contactables.LABEL, label.label)
        else -> withCustomLabel(resources, label)
    }
}

internal fun ContentProviderOperation.Builder.withPostalAddressLabel(
    label: Label, resources: Resources
): ContentProviderOperation.Builder {
    return when (label) {
        Label.LocationHome -> withValue(Contactables.TYPE, StructuredPostal.TYPE_HOME)
        Label.LocationWork -> withValue(Contactables.TYPE, StructuredPostal.TYPE_WORK)
        Label.Other -> withValue(Contactables.TYPE, StructuredPostal.TYPE_OTHER)
        is Label.Custom -> withValue(Contactables.TYPE, Contactables.TYPE_CUSTOM)
            .withValue(Contactables.LABEL, label.label)
        else -> withCustomLabel(resources, label)
    }
}

internal fun ContentProviderOperation.Builder.withImLabel(
    label: Label, resources: Resources
): ContentProviderOperation.Builder {
    return when (label) {
        Label.LocationHome -> withValue(Contactables.TYPE, Im.TYPE_HOME)
        Label.LocationWork -> withValue(Contactables.TYPE, Im.TYPE_WORK)
        Label.Other -> withValue(Contactables.TYPE, Im.TYPE_OTHER)
        is Label.Custom -> withValue(Contactables.TYPE, Contactables.TYPE_CUSTOM)
            .withValue(Contactables.LABEL, label.label)
        else -> withCustomLabel(resources, label)
    }
}

internal fun ContentProviderOperation.Builder.withWebAddressLabel(
    label: Label, resources: Resources
): ContentProviderOperation.Builder {
    return when (label) {
        Label.WebsiteProfile -> withValue(Contactables.TYPE, Website.TYPE_PROFILE)
        Label.LocationWork -> withValue(Contactables.TYPE, Website.TYPE_WORK)
        Label.WebsiteHomePage -> withValue(Contactables.TYPE, Website.TYPE_HOMEPAGE)
        Label.WebsiteFtp -> withValue(Contactables.TYPE, Website.TYPE_FTP)
        Label.WebsiteBlog -> withValue(Contactables.TYPE, Website.TYPE_BLOG)
        Label.Other -> withValue(Contactables.TYPE, Website.TYPE_OTHER)
        is Label.Custom -> withValue(Contactables.TYPE, Contactables.TYPE_CUSTOM)
            .withValue(Contactables.LABEL, label.label)
        else -> withCustomLabel(resources, label)
    }
}

internal fun ContentProviderOperation.Builder.withEventLabel(
    label: Label, resources: Resources
): ContentProviderOperation.Builder {
    return when (label) {
        Label.DateAnniversary -> withValue(Contactables.TYPE, Event.TYPE_ANNIVERSARY)
        Label.DateBirthday -> withValue(Contactables.TYPE, Event.TYPE_BIRTHDAY)
        Label.Other -> withValue(Contactables.TYPE, Event.TYPE_OTHER)
        is Label.Custom -> withValue(Contactables.TYPE, Contactables.TYPE_CUSTOM)
            .withValue(Contactables.LABEL, label.label)
        else -> withCustomLabel(resources, label)
    }
}

private fun ContentProviderOperation.Builder.withCustomLabel(
    resources: Resources,
    stringResId: Int
): ContentProviderOperation.Builder {
    return withValue(Contactables.TYPE, Contactables.TYPE_CUSTOM)
        .withValue(Contactables.LABEL, resources.getString(stringResId))
}


private fun ContentProviderOperation.Builder.withCustomLabel(
    resources: Resources,
    column: ContactColumn,
    type: Int
): ContentProviderOperation.Builder {
    val typeResource = when (column) {
        ContactColumn.Events -> Event.getTypeResource(type)
        ContactColumn.ImAddresses -> Im.getTypeLabelResource(type)
        ContactColumn.Phones -> Phone.getTypeLabelResource(type)
        ContactColumn.Mails -> Email.getTypeLabelResource(type)
        ContactColumn.Organization -> Organization.getTypeLabelResource(type)
        ContactColumn.PostalAddresses -> StructuredPostal.getTypeLabelResource(type)
        ContactColumn.Relations -> Relation.getTypeLabelResource(type)
        ContactColumn.SipAddresses -> CommonDataKinds.SipAddress.getTypeLabelResource(type)
        ContactColumn.WebAddresses -> error("WebAddress label case was unhandled")
        ContactColumn.Note,
        ContactColumn.Names,
        ContactColumn.Nickname,
        ContactColumn.CustomDataItems,
        ContactColumn.Image,
        ContactColumn.GroupMemberships -> error(
            "Tried to get label for a ${column.javaClass.simpleName}"
        )
    }

    return withValue(Contactables.TYPE, Contactables.TYPE_CUSTOM)
        .withValue(Contactables.LABEL, resources.getString(typeResource))
}

private fun ContentProviderOperation.Builder.withCustomLabel(
    resources: Resources,
    label: Label
): ContentProviderOperation.Builder {
    return when (label) {
        is Label.Custom -> withValue(Contactables.TYPE, Contactables.TYPE_CUSTOM)
            .withValue(Contactables.LABEL, label.label)
        Label.DateBirthday -> {
            val typeResource = Event.getTypeResource(Event.TYPE_BIRTHDAY)
            withValue(Contactables.TYPE, Contactables.TYPE_CUSTOM)
                .withValue(Contactables.LABEL, resources.getString(typeResource))
        }
        Label.DateAnniversary -> withCustomLabel(
            resources, ContactColumn.Events, Event.TYPE_ANNIVERSARY
        )
        Label.RelationBrother -> withCustomLabel(
            resources, ContactColumn.Relations, Relation.TYPE_BROTHER
        )
        Label.RelationChild -> withCustomLabel(
            resources, ContactColumn.Relations, Relation.TYPE_CHILD
        )
        Label.RelationDomesticPartner -> withCustomLabel(
            resources, ContactColumn.Relations, Relation.TYPE_DOMESTIC_PARTNER
        )
        Label.RelationFather -> withCustomLabel(
            resources, ContactColumn.Relations, Relation.TYPE_FATHER
        )
        Label.RelationFriend -> withCustomLabel(
            resources, ContactColumn.Relations, Relation.TYPE_FRIEND
        )
        Label.RelationManager -> withCustomLabel(
            resources, ContactColumn.Relations, Relation.TYPE_MANAGER
        )
        Label.RelationMother -> withCustomLabel(
            resources, ContactColumn.Relations, Relation.TYPE_MOTHER
        )
        Label.RelationParent -> withCustomLabel(
            resources, ContactColumn.Relations, Relation.TYPE_PARENT
        )
        Label.RelationPartner -> withCustomLabel(
            resources, ContactColumn.Relations, Relation.TYPE_PARTNER
        )
        Label.RelationReferredBy -> withCustomLabel(
            resources, ContactColumn.Relations, Relation.TYPE_REFERRED_BY
        )
        Label.RelationRelative -> withCustomLabel(
            resources, ContactColumn.Relations, Relation.TYPE_RELATIVE
        )
        Label.RelationSister -> withCustomLabel(
            resources, ContactColumn.Relations, Relation.TYPE_SISTER
        )
        Label.RelationSpouse -> withCustomLabel(
            resources, ContactColumn.Relations, Relation.TYPE_SPOUSE
        )
        Label.WebsiteBlog -> withCustomLabel(resources, R.string.blog)
        Label.WebsiteFtp -> withCustomLabel(resources, R.string.ftp)
        Label.WebsiteHomePage -> withCustomLabel(resources, R.string.home_page)
        Label.WebsiteProfile -> withCustomLabel(resources, R.string.profile)
        Label.LocationHome -> withCustomLabel(
            resources, ContactColumn.PostalAddresses, StructuredPostal.TYPE_HOME
        )
        Label.LocationWork -> withCustomLabel(
            resources, ContactColumn.PostalAddresses, StructuredPostal.TYPE_WORK
        )
        Label.Main -> withCustomLabel(resources, ContactColumn.Phones, Phone.TYPE_MAIN)
        Label.Other -> withCustomLabel(resources, ContactColumn.Phones, Phone.TYPE_OTHER)
        Label.PhoneNumberAssistant -> withCustomLabel(
            resources, ContactColumn.Phones, Phone.TYPE_ASSISTANT
        )
        Label.PhoneNumberCallback -> withCustomLabel(
            resources, ContactColumn.Phones, Phone.TYPE_CALLBACK
        )
        Label.PhoneNumberCar -> withCustomLabel(resources, ContactColumn.Phones, Phone.TYPE_CAR)
        Label.PhoneNumberCompanyMain -> withCustomLabel(
            resources, ContactColumn.Phones, Phone.TYPE_COMPANY_MAIN
        )
        Label.PhoneNumberFaxHome -> withCustomLabel(
            resources, ContactColumn.Phones, Phone.TYPE_FAX_HOME
        )
        Label.PhoneNumberFaxWork -> withCustomLabel(
            resources, ContactColumn.Phones, Phone.TYPE_FAX_WORK
        )
        Label.PhoneNumberIsdn -> withCustomLabel(resources, ContactColumn.Phones, Phone.TYPE_ISDN)
        Label.PhoneNumberMms -> withCustomLabel(
            resources, ContactColumn.Phones, Phone.TYPE_MMS
        )
        Label.PhoneNumberMobile -> withCustomLabel(
            resources, ContactColumn.Phones, Phone.TYPE_MOBILE
        )
        Label.PhoneNumberOtherFax -> withCustomLabel(
            resources, ContactColumn.Phones, Phone.TYPE_OTHER_FAX
        )
        Label.PhoneNumberPager -> withCustomLabel(
            resources, ContactColumn.Phones, Phone.TYPE_PAGER
        )
        Label.PhoneNumberRadio -> withCustomLabel(
            resources, ContactColumn.Phones, Phone.TYPE_RADIO
        )
        Label.PhoneNumberTelex -> withCustomLabel(
            resources, ContactColumn.Phones, Phone.TYPE_TELEX
        )
        Label.PhoneNumberTtyTdd -> withCustomLabel(
            resources, ContactColumn.Phones, Phone.TYPE_TTY_TDD
        )
        Label.PhoneNumberWorkMobile -> withCustomLabel(
            resources, ContactColumn.Phones, Phone.TYPE_WORK_MOBILE
        )
        Label.PhoneNumberWorkPager -> withCustomLabel(
            resources, ContactColumn.Phones, Phone.TYPE_WORK_PAGER
        )
    }
}
