package com.alexstyl.contactstore.utils

import com.alexstyl.contactstore.EventDate

internal interface DateParser {
    fun parse(rawDate: String?): EventDate?
}