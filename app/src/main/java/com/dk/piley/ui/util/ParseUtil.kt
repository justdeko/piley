package com.dk.piley.ui.util

import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

fun LocalDateTime.toDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.clear()
    calendar.set(year, monthValue - 1, dayOfMonth, hour, minute, second)
    return calendar.time
}

fun Date.toLocalDateTime(): LocalDateTime? =
    Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDateTime()

fun LocalDateTime.dateTimeString(): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm") // TODO: locale specific
    return this.format(formatter)
}

fun LocalDateTime.dateString(): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy") // TODO: locale specific
    return this.toLocalDate().format(formatter)
}

fun LocalDateTime.timeString(): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm") // TODO: locale specific
    return this.toLocalTime().format(formatter)
}

val utcZoneId: ZoneId = ZoneId.ofOffset("UTC", ZoneOffset.UTC)
