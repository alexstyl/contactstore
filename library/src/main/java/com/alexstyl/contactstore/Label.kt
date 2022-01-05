package com.alexstyl.contactstore

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

    public data class Custom(val label: String) : Label()
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
}
