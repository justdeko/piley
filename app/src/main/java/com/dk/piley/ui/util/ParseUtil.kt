package com.dk.piley.ui.util

import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter

fun Long.toLocalDateTime(): LocalDateTime {
    val zoneId = ZoneId.systemDefault()
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(this), zoneId)
}

fun LocalDateTime.toTimestamp(): Long {
    val zoneId = ZoneId.systemDefault()
    return atZone(zoneId).toInstant().toEpochMilli()
}

fun LocalDateTime.dateTimeString(): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm") // TODO: locale specific
    return this.format(formatter)
}

val utcZoneId: ZoneId = ZoneId.ofOffset("UTC", ZoneOffset.UTC)
