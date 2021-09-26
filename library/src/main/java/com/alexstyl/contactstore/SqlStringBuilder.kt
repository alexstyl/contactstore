package com.alexstyl.contactstore

private const val TEN = 10
private const val SEPARATOR = "-"
private const val ZERO = "0"

internal fun sqlString(forDate: EventDate): String {
    return buildString {
        append(forDate.year ?: SEPARATOR)
        append(SEPARATOR)
        appendMonth(forDate)
        append(SEPARATOR)
        appendDayOfMonth(forDate)
    }
}

private fun StringBuilder.appendMonth(date: EventDate) {
    val isSingleDigit = isSingleDigit(date.month)
    if (isSingleDigit) {
        append(ZERO)
    }
    append(date.month)
}

private fun StringBuilder.appendDayOfMonth(date: EventDate) {
    val isSingleDigit = isSingleDigit(date.dayOfMonth)
    if (isSingleDigit) {
        append(ZERO)
    }
    append(date.dayOfMonth)
}

private fun isSingleDigit(number: Int): Boolean {
    return number < TEN
}
