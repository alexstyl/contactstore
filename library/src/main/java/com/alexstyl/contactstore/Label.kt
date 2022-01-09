package com.alexstyl.contactstore

import android.content.res.Resources
import android.provider.ContactsContract.CommonDataKinds.Event
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.Relation
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal

public sealed class Label {
    public object Main : Label()

    public object PhoneNumberMobile : Label()
    public object LocationHome : Label()
    public object LocationWork : Label()
    public object PhoneNumberPager : Label()
    public object PhoneNumberCar : Label()
    public object PhoneNumberFaxWork : Label()
    public object PhoneNumberFaxHome : Label()
    public object PhoneNumberCallback : Label()
    public object PhoneNumberCompanyMain : Label()
    public object PhoneNumberIsdn : Label()

    public object PhoneNumberOtherFax : Label()
    public object PhoneNumberRadio : Label()
    public object PhoneNumberTelex : Label()
    public object PhoneNumberTtyTdd : Label()
    public object PhoneNumberWorkMobile : Label()
    public object PhoneNumberWorkPager : Label()
    public object PhoneNumberAssistant : Label()
    public object PhoneNumberMms : Label()

    public object DateBirthday : Label()
    public object DateAnniversary : Label()

    public object WebsiteBlog : Label()
    public object WebsiteFtp : Label()
    public object WebsiteHomePage : Label()
    public object WebsiteProfile : Label()

    public object Other : Label()
    public object RelationBrother : Label()
    public object RelationChild : Label()
    public object RelationDomesticPartner : Label()
    public object RelationFather : Label()
    public object RelationFriend : Label()
    public object RelationManager : Label()
    public object RelationMother : Label()
    public object RelationParent : Label()
    public object RelationPartner : Label()
    public object RelationReferredBy : Label()
    public object RelationRelative : Label()
    public object RelationSister : Label()
    public object RelationSpouse : Label()
    public data class Custom(val label: String) : Label()
}

/**
 * Return a localized String of the Label that can be used for UI purposes.
 */
public fun Label.getLocalizedString(resources: Resources): String {
    return when (this) {
        is Label.Custom -> this.label
        Label.DateAnniversary -> resources.getString(Event.getTypeResource(Event.TYPE_ANNIVERSARY))
        Label.DateBirthday -> resources.getString(Event.getTypeResource(Event.TYPE_BIRTHDAY))
        Label.LocationHome -> resources.getString(Event.getTypeResource(Event.TYPE_BIRTHDAY))
        Label.LocationWork -> resources.getString(StructuredPostal.getTypeLabelResource(StructuredPostal.TYPE_WORK))
        Label.Main -> resources.getString(Phone.getTypeLabelResource(Phone.TYPE_MAIN))
        Label.Other -> resources.getString(Phone.getTypeLabelResource(Phone.TYPE_OTHER))
        Label.PhoneNumberAssistant -> resources.getString(Phone.getTypeLabelResource(Phone.TYPE_ASSISTANT))
        Label.PhoneNumberCallback -> resources.getString(Phone.getTypeLabelResource(Phone.TYPE_CALLBACK))
        Label.PhoneNumberCar -> resources.getString(Phone.getTypeLabelResource(Phone.TYPE_CAR))
        Label.PhoneNumberCompanyMain -> resources.getString(Phone.getTypeLabelResource(Phone.TYPE_COMPANY_MAIN))
        Label.PhoneNumberFaxHome -> resources.getString(Phone.getTypeLabelResource(Phone.TYPE_FAX_HOME))
        Label.PhoneNumberFaxWork -> resources.getString(Phone.getTypeLabelResource(Phone.TYPE_FAX_WORK))
        Label.PhoneNumberIsdn -> resources.getString(Phone.getTypeLabelResource(Phone.TYPE_ISDN))
        Label.PhoneNumberMms -> resources.getString(Phone.getTypeLabelResource(Phone.TYPE_MMS))
        Label.PhoneNumberMobile -> resources.getString(Phone.getTypeLabelResource(Phone.TYPE_MOBILE))
        Label.PhoneNumberOtherFax -> resources.getString(Phone.getTypeLabelResource(Phone.TYPE_OTHER_FAX))
        Label.PhoneNumberPager -> resources.getString(Phone.getTypeLabelResource(Phone.TYPE_PAGER))
        Label.PhoneNumberRadio -> resources.getString(Phone.getTypeLabelResource(Phone.TYPE_RADIO))
        Label.PhoneNumberTelex -> resources.getString(Phone.getTypeLabelResource(Phone.TYPE_TELEX))
        Label.PhoneNumberTtyTdd -> resources.getString(Phone.getTypeLabelResource(Phone.TYPE_TTY_TDD))
        Label.PhoneNumberWorkMobile -> resources.getString(Phone.getTypeLabelResource(Phone.TYPE_WORK_MOBILE))
        Label.PhoneNumberWorkPager -> resources.getString(Phone.getTypeLabelResource(Phone.TYPE_WORK_PAGER))
        Label.RelationBrother -> resources.getString(Relation.getTypeLabelResource(Relation.TYPE_BROTHER))
        Label.RelationChild -> resources.getString(Relation.getTypeLabelResource(Relation.TYPE_CHILD))
        Label.RelationDomesticPartner -> resources.getString(Relation.getTypeLabelResource(Relation.TYPE_DOMESTIC_PARTNER))
        Label.RelationFather -> resources.getString(Relation.getTypeLabelResource(Relation.TYPE_FATHER))
        Label.RelationFriend -> resources.getString(Relation.getTypeLabelResource(Relation.TYPE_FRIEND))
        Label.RelationManager -> resources.getString(Relation.getTypeLabelResource(Relation.TYPE_MANAGER))
        Label.RelationMother -> resources.getString(Relation.getTypeLabelResource(Relation.TYPE_MOTHER))
        Label.RelationParent -> resources.getString(Relation.getTypeLabelResource(Relation.TYPE_PARENT))
        Label.RelationPartner -> resources.getString(Relation.getTypeLabelResource(Relation.TYPE_PARTNER))
        Label.RelationReferredBy -> resources.getString(Relation.getTypeLabelResource(Relation.TYPE_REFERRED_BY))
        Label.RelationRelative -> resources.getString(Relation.getTypeLabelResource(Relation.TYPE_RELATIVE))
        Label.RelationSister -> resources.getString(Relation.getTypeLabelResource(Relation.TYPE_SISTER))
        Label.RelationSpouse -> resources.getString(Relation.getTypeLabelResource(Relation.TYPE_SPOUSE))
        Label.WebsiteBlog -> resources.getString(R.string.blog)
        Label.WebsiteFtp -> resources.getString(R.string.ftp)
        Label.WebsiteHomePage -> resources.getString(R.string.home_page)
        Label.WebsiteProfile -> resources.getString(R.string.profile)
    }
}
