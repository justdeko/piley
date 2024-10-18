package com.dk.piley.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

/**
 * Convert Instant to local date time using a specific zone id
 *
 * @param zoneId the zone id to use for the local date time, system default if not specified
 * @return local date time within specified zone id
 */
fun Instant.toLocalDateTime(zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime {
    return LocalDateTime.ofInstant(this, zoneId)
}

/**
 * Convert local date time to an instant with offset
 *
 * @param zoneId zone id to calculate the offset with
 * @return Instant with local date time offset considered.
 * So if the zone id was in a +1 zone, the instant will be 1 hour behind the date time.
 */
fun LocalDateTime.toInstantWithOffset(zoneId: ZoneId = ZoneId.systemDefault()): Instant {
    val offset = zoneId.rules.getOffset(this)
    return this.toInstant(offset)
}

/**
 * Convert [LocalDateTime] entity to formatted string
 *
 * @return localized date time string
 */
fun LocalDateTime.dateTimeString(): String {
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
    return this.format(formatter)
}

/**
 * Convert [LocalDateTime] entity to formatted string, but separated by a newline
 *
 * @return localized date and localized time each on a separate line
 */
fun LocalDateTime.dateTimeStringNewLine(): String {
    val formatterDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
    val formatterTime = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    return "${this.format(formatterDate)}\n${this.format(formatterTime)}"
}

/**
 * Convert [LocalTime] entity to string
 *
 * @return localized time string
 */
fun LocalTime.timeString(): String {
    val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    return this.format(formatter)
}

/**
 * Convert [LocalDate] entity to string
 *
 * @return localized date string
 */
fun LocalDate.dateString(): String {
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
    return this.format(formatter)
}

/**
 * Get the abbreviated display name of the last seven days of the week (including today)
 *
 * @param date the starting date for the last seven days
 * @return localized list of days of week in abbreviated form
 */
fun lastSevenDays(date: LocalDate): List<String> = (0..6)
    .map {
        date.minusDays(it.toLong()).dayOfWeek.getDisplayName(
            TextStyle.SHORT,
            Locale.getDefault()
        )
    }
    .reversed()

/**
 * Utc time zone id
 */
val utcZoneId: ZoneId = ZoneId.ofOffset("UTC", ZoneOffset.UTC)
