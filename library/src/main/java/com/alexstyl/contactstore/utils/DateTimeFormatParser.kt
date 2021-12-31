package com.alexstyl.contactstore.utils

import com.alexstyl.contactstore.EventDate
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

internal class DateTimeFormatParser : DateParser {
    override fun parse(rawDate: String?): EventDate? {
        if (rawDate == null) return null
        val parsePosition = ParsePosition(0)
        val parsedDate =
            FULL_DATE_FORMAT.parse(rawDate, parsePosition) ?: NO_YEAR_DATE_FORMAT.parse(
                rawDate,
                parsePosition
            )
        return parsedDate
            ?.let { date ->
                val calendar = Calendar.getInstance().apply {
                    time = date
                }

                EventDate(
                    dayOfMonth = calendar[Calendar.DAY_OF_MONTH],
                    month = calendar[Calendar.MONTH] + 1,
                    year = calendar[Calendar.YEAR]
                )
            }
    }

    private companion object {
        val FULL_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val NO_YEAR_DATE_FORMAT = SimpleDateFormat("--MM-dd", Locale.US)
    }
}
