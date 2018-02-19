package org.stepik.core.auth.webkit.network


import java.util.*
import java.util.TimeZone.getTimeZone

/**
 * An RFC 6265-compliant date parser.
 */
internal object DateParser {
    private class Time internal constructor(internal val hour: Int, internal val minute: Int, internal val second: Int)

    private val DELIMITER_PATTERN = "[\\x09\\x20-\\x2F\\x3B-\\x40\\x5B-\\x60\\x7B-\\x7E]+".toRegex()
    private val TIME_PATTERN = "(\\d{1,2}):(\\d{1,2}):(\\d{1,2})(?:[^\\d].*)*".toRegex()
    private val DAY_OF_MONTH_PATTERN = "(\\d{1,2})(?:[^\\d].*)*".toRegex()
    private val YEAR_PATTERN = "(\\d{2,4})(?:[^\\d].*)*".toRegex()
    private val MONTH_MAP = mapOf(
            "jan" to 0,
            "feb" to 1,
            "mar" to 2,
            "apr" to 3,
            "may" to 4,
            "jun" to 5,
            "jul" to 6,
            "aug" to 7,
            "sep" to 8,
            "oct" to 9,
            "nov" to 10,
            "dec" to 11
    )

    fun parse(date: String): Long? {
        var time: DateParser.Time? = null
        var dayOfMonth: Int? = null
        var month: Int? = null
        var year: Int? = null
        val tokens = DELIMITER_PATTERN.split(date)
        for (token in tokens) {
            if (token.isEmpty()) {
                continue
            }

            if (time == null) {
                time = parseTime(token)
                if (time != null) {
                    continue
                }
            }

            if (dayOfMonth == null) {
                dayOfMonth = parseDayOfMonth(token)
                if (dayOfMonth != null) {
                    continue
                }
            }

            if (month == null) {
                month = parseMonth(token)
                if (month != null) {
                    continue
                }
            }

            if (year == null) {
                year = parseYear(token)
            }
        }

        if (year != null) {
            year += when (year) {
                in 70..99 -> 1900
                in 0..69 -> 2000
                else -> 0
            }
        }

        if (time == null || dayOfMonth == null || month == null || year == null
                || dayOfMonth !in 1..31
                || year < 1601
                || time.hour > 23
                || time.minute > 59
                || time.second > 59) {
            return null
        }

        val calendar = Calendar.getInstance(getTimeZone("UTC"), Locale.US)
        calendar.isLenient = false
        calendar.clear()

        calendar.set(year, month, dayOfMonth, time.hour, time.minute, time.second)

        return try {
            calendar.timeInMillis
        } catch (ex: Exception) {
            null
        }

    }

    private fun parseTime(token: String): DateParser.Time? {
        val matcher = TIME_PATTERN.matchEntire(token)
        val groups = matcher?.groups ?: return null
        return DateParser.Time(
                groups[1]!!.value.toInt(),
                groups[2]!!.value.toInt(),
                groups[3]!!.value.toInt())
    }

    private fun parseDayOfMonth(token: String): Int? {
        val matcher = DAY_OF_MONTH_PATTERN.matchEntire(token)
        return matcher?.groups?.get(1)?.value?.toInt()
    }

    private fun parseMonth(token: String): Int? {
        return if (token.length >= 3) {
            MONTH_MAP[token.substring(0, 3).toLowerCase()]
        } else {
            null
        }
    }

    private fun parseYear(token: String): Int? {
        val matcher = YEAR_PATTERN.matchEntire(token)
        return matcher?.groups?.get(1)?.value?.toInt()
    }
}
