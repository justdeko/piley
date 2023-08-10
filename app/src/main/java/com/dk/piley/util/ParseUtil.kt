package com.dk.piley.util

import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import java.util.Locale

fun Long.toLocalDateTime(): LocalDateTime {
    val zoneId = ZoneId.systemDefault()
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(this), zoneId)
}

fun Instant.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(this, ZoneId.systemDefault())
}

fun LocalDateTime.toInstant(): Instant {
    val offset = ZoneId.systemDefault().rules.getOffset(this)
    return this.toInstant(offset)
}

fun LocalDateTime.toTimestamp(): Long {
    val zoneId = ZoneId.systemDefault()
    return atZone(zoneId).toInstant().toEpochMilli()
}

fun LocalDateTime.dateTimeString(): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm") // TODO: locale specific
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
