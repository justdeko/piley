package com.dk.piley.util

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

/**
 * Convert Instant to local date time using a specific timezone
 *
 * @param timeZone the time zone
 * @return local date time within specified zone id
 */
fun Instant.toLocalDateTime(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime {
    return this.toLocalDateTime(timeZone)
}

/**
 * Convert local date time to an instant with offset
 *
 * @param timeZone timezone to calculate the offset with
 * @return Instant with local date time offset considered.
 * So if the zone id was in a +1 zone, the instant will be 1 hour behind the date time.
 */
fun LocalDateTime.toInstantWithOffset(timeZone: TimeZone = TimeZone.currentSystemDefault()): Instant {
    return this.toInstant(timeZone)
}

/**
 * Convert [LocalDateTime] entity to formatted string
 *
 * @return localized date time string
 */
fun LocalDateTime.dateTimeString(): String {
    val format = LocalDateTime.Format {
        hour()
        chars(":")
        minute()
        chars(" ")
        dayOfMonth()
        chars(".")
        monthNumber()
        chars(".")
        yearTwoDigits(2000)
    }
    return this.format(format)
}

/**
 * Convert [LocalDateTime] entity to formatted string, but separated by a newline
 *
 * @return localized date and localized time each on a separate line
 */
fun LocalDateTime.dateTimeStringNewLine(): String {
    return "${this.date.dateString()}\n${this.time.timeString()}"
}

/**
 * Convert [LocalTime] entity to string
 *
 * @return localized time string
 */
fun LocalTime.timeString(): String {
    return this.format(LocalTime.Format { hour();char(':');minute() })
}

/**
 * Convert [LocalDate] entity to string
 *
 * @return localized date string
 */
fun LocalDate.dateString(): String {
    return this.format(LocalDate.Formats.ISO)
}

/**
 * Get the abbreviated display name of the last seven days of the week (including today)
 *
 * @param date the starting date for the last seven days
 * @return localized list of days of week in abbreviated form
 */
fun lastSevenDays(
    date: LocalDate,
    timeZone: TimeZone = TimeZone.UTC
): List<String> = (0..6)
    .map {
        date.minus(it, DateTimeUnit.DAY).dayOfWeek.name.substring(0..1)
    }
    .reversed()

/**
 * Utc time zone id
 */
val utcZone: TimeZone = TimeZone.UTC
