package org.stepik.core.auth.webkit.network


import java.text.ParseException
import java.util.*
import java.util.regex.Pattern

/**
 * An RFC 6265-compliant date parser.
 */
internal class DateParser
/**
 * The private default constructor. Ensures non-instantiability.
 */
private constructor() {


    init {
        throw AssertionError()
    }

    /**
     * Container for parsed time.
     */
    private class Time internal constructor(internal val hour: Int, internal val minute: Int, internal val second: Int)

    companion object {
        private val DELIMITER_PATTERN = Pattern.compile(
                "[\\x09\\x20-\\x2F\\x3B-\\x40\\x5B-\\x60\\x7B-\\x7E]+")
        private val TIME_PATTERN = Pattern.compile(
                "(\\d{1,2}):(\\d{1,2}):(\\d{1,2})(?:[^\\d].*)*")
        private val DAY_OF_MONTH_PATTERN = Pattern.compile(
                "(\\d{1,2})(?:[^\\d].*)*")
        private val YEAR_PATTERN = Pattern.compile(
                "(\\d{2,4})(?:[^\\d].*)*")
        private val MONTH_MAP: Map<String, Int>

        init {
            val map = HashMap<String, Int>(12)
            map["jan"] = 0
            map["feb"] = 1
            map["mar"] = 2
            map["apr"] = 3
            map["may"] = 4
            map["jun"] = 5
            map["jul"] = 6
            map["aug"] = 7
            map["sep"] = 8
            map["oct"] = 9
            map["nov"] = 10
            map["dec"] = 11
            MONTH_MAP = Collections.unmodifiableMap(map)
        }

        /**
         * Parses a given date string as required by RFC 6265.
         *
         * @param date the string to parse
         * @return the difference, measured in milliseconds, between the parsed
         * date and midnight, January 1, 1970 UTC
         * @throws ParseException if `date` cannot be parsed
         */
        @Throws(ParseException::class)
        fun parse(date: String): Long {
            var time: DateParser.Time? = null
            var dayOfMonth: Int? = null
            var month: Int? = null
            var year: Int? = null
            val tokens = DELIMITER_PATTERN.split(date, 0)
            for (token in tokens) {
                if (token.isEmpty()) {
                    continue
                }

                if (time == null) {
                    val timeTmp = parseTime(token)
                    if (timeTmp != null) {
                        time = timeTmp
                        continue
                    }
                }

                if (dayOfMonth == null) {
                    val dayOfMonthTmp = parseDayOfMonth(token)
                    if (dayOfMonthTmp != null) {
                        dayOfMonth = dayOfMonthTmp
                        continue
                    }
                }

                if (month == null) {
                    val monthTmp = parseMonth(token)
                    if (monthTmp != null) {
                        month = monthTmp
                        continue
                    }
                }

                if (year == null) {
                    val yearTmp = parseYear(token)
                    if (yearTmp != null) {
                        year = yearTmp
                    }
                }
            }

            if (year != null) {
                if (year in 70..99) {
                    year += 1900
                } else if (year in 0..69) {
                    year += 2000
                }
            }

            if (time == null || dayOfMonth == null || month == null || year == null
                    || dayOfMonth < 1 || dayOfMonth > 31
                    || year < 1601
                    || time.hour > 23
                    || time.minute > 59
                    || time.second > 59) {
                throw ParseException("Error parsing date", 0)
            }

            val calendar = Calendar.getInstance(
                    TimeZone.getTimeZone("UTC"), Locale.US)
            calendar.isLenient = false
            calendar.clear()

            calendar.set(year, month, dayOfMonth, time.hour, time.minute, time.second)

            try {
                return calendar.timeInMillis
            } catch (ex: Exception) {
                val pe = ParseException("Error parsing date", 0)
                pe.initCause(ex)
                throw pe
            }

        }

        /**
         * Parses a token as a time string.
         */
        private fun parseTime(token: String): DateParser.Time? {
            val matcher = TIME_PATTERN.matcher(token)
            return if (matcher.matches()) {
                DateParser.Time(
                        Integer.parseInt(matcher.group(1)),
                        Integer.parseInt(matcher.group(2)),
                        Integer.parseInt(matcher.group(3)))
            } else {
                null
            }
        }

        /**
         * Parses a token as a day of month.
         */
        private fun parseDayOfMonth(token: String): Int? {
            val matcher = DAY_OF_MONTH_PATTERN.matcher(token)
            return if (matcher.matches()) {
                Integer.parseInt(matcher.group(1))
            } else {
                null
            }
        }

        /**
         * Parses a token as a month.
         */
        private fun parseMonth(token: String): Int? {
            return if (token.length >= 3) {
                MONTH_MAP[token.substring(0, 3).toLowerCase()]
            } else {
                null
            }
        }

        /**
         * Parses a token as a year.
         */
        private fun parseYear(token: String): Int? {
            val matcher = YEAR_PATTERN.matcher(token)
            return if (matcher.matches()) {
                Integer.parseInt(matcher.group(1))
            } else {
                null
            }
        }
    }
}
