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

fun Instant.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(this, ZoneId.systemDefault())
}

fun LocalDateTime.toInstant(): Instant {
    val offset = ZoneId.systemDefault().rules.getOffset(this)
    return this.toInstant(offset)
}

fun LocalDateTime.dateTimeString(): String {
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
    return this.format(formatter)
}

fun LocalDateTime.dateTimeStringNewLine(): String {
    val formatterDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
    val formatterTime = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    return "${this.format(formatterDate)}\n${this.format(formatterTime)}"
}

fun LocalTime.timeString(): String {
    val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    return this.format(formatter)
}

fun LocalDate.dateString(): String {
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
    return this.format(formatter)
}

fun lastSevenDays(date: LocalDate): List<String> = (0..6)
    .map {
        date.minusDays(it.toLong()).dayOfWeek.getDisplayName(
            TextStyle.SHORT,
            Locale.getDefault()
        )
    }
    .reversed()

val utcZoneId: ZoneId = ZoneId.ofOffset("UTC", ZoneOffset.UTC)
