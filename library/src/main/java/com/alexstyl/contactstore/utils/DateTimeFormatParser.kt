package com.alexstyl.contactstore.utils

import com.alexstyl.contactstore.EventDate
import org.joda.time.format.DateTimeFormat
import java.util.Locale

internal class DateTimeFormatParser : DateParser {
    override fun parse(rawDate: String?): EventDate? {
        if (rawDate == null) return null
        for (locale in arrayOf(Locale.getDefault(), Locale.US)) {
            for (format in SUPPORTED_FORMATS) {
                val formatter = DateTimeFormat.forPattern(format)
                    .withLocale(locale)
                    .withDefaultYear(NO_YEAR)
                try {
                    val parsedDate = formatter.parseLocalDate(rawDate)
                    val dayOfMonth = parsedDate.dayOfMonth
                    val month = parsedDate.monthOfYear
                    val year = parsedDate.year

                    return EventDate(
                        dayOfMonth = dayOfMonth,
                        month = month,
                        year = year.takeIf { year != NO_YEAR })
                } catch (e: IllegalArgumentException) {
                    if (isNotAboutInvalidFormat(e)) {
                        // unexpected error - throw it
                        throw e
                    }
                }
            }
        }
        return null
    }

    private fun isNotAboutInvalidFormat(e: IllegalArgumentException): Boolean {
        return e.message?.contains("Invalid format")?.not() ?: true
    }

    private companion object {
        /*
         * The first year in JodaTime which contains the date 29 of February.
         */
        const val NO_YEAR = 4
        val SUPPORTED_FORMATS = listOf(
            "yyyy-MM-dd",
            "MMM dd, yyyy",
            "MMM dd yyyy",
            "dd MMM. yyyy",
            "dd MMM yyyy",
            "yyyyMMdd",
            "dd MMM yyyy",
            "d MMM yyyy",
            "dd/MM/yyyy",
            "yyyy-MM-dd HH:mm:ss.SSSZ",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
            "dd-MM-yyyy",
            "dd/MMMM/yyyy",
            "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyyMMdd'T'HHmmssZ",
            // no year
            "--MM-dd",
            "MMM dd",
            "dd MMM",
            "dd MMM."
        )
    }
}
