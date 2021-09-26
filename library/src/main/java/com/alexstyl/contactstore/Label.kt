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
}
