package com.alexstyl.contactstore

sealed class Label {
    object Main : Label()

    object PhoneNumberMobile : Label()
    object LocationHome : Label()
    object LocationWork : Label()
    object PhoneNumberPager : Label()
    object PhoneNumberCar : Label()
    object PhoneNumberFaxWork : Label()
    object PhoneNumberFaxHome : Label()
    object PhoneNumberCallback : Label()
    object PhoneNumberCompanyMain : Label()
    object PhoneNumberIsdn : Label()

    object PhoneNumberOtherFax : Label()
    object PhoneNumberRadio : Label()
    object PhoneNumberTelex : Label()
    object PhoneNumberTtyTdd : Label()
    object PhoneNumberWorkMobile : Label()
    object PhoneNumberWorkPager : Label()
    object PhoneNumberAssistant : Label()
    object PhoneNumberMms : Label()

    object DateBirthday : Label()
    object DateAnniversary : Label()

    object WebsiteBlog : Label()
    object WebsiteFtp : Label()
    object WebsiteHomePage : Label()
    object WebsiteProfile : Label()

    data class Custom(val label: String) : Label()
    object Other : Label()
    object RelationBrother : Label()
    object RelationChild : Label()
    object RelationDomesticPartner : Label()
    object RelationFather : Label()
    object RelationFriend : Label()
    object RelationManager : Label()
    object RelationMother : Label()
    object RelationParent : Label()
    object RelationPartner : Label()
    object RelationReferredBy : Label()
    object RelationRelative : Label()
    object RelationSister : Label()
    object RelationSpouse : Label()
}
