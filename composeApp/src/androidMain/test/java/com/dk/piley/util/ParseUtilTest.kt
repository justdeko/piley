package com.dk.piley.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ParseUtilTest {

    private val sampleDateTime: LocalDateTime = LocalDateTime.of(2023, 8, 9, 13, 14)

    @Test
    fun toLocalDateTime() {
        val instant = Instant.parse("2023-08-14T12:02:02.000Z")
        val dateTime = instant.toLocalDateTime(utcZoneId)
        assertEquals("2023-08-14", dateTime.toLocalDate().toString())
        assertEquals("12:02:02", dateTime.toLocalTime().toString())
    }

    @Test
    fun toInstantWithOffset() {
        val instant = sampleDateTime.toInstantWithOffset(utcZoneId)
        assertEquals("2023-08-09T13:14:00Z", instant.toString())
    }

    @Test
    fun dateTimeString() {
        val dateTimeString = sampleDateTime.dateTimeString()
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
        assertEquals(sampleDateTime.format(formatter), dateTimeString)
    }

    @Test
    fun dateTimeStringNewLine() {
        val dateTimeString = sampleDateTime.dateTimeStringNewLine()
        val formatterDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
        val formatterTime = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        assertEquals(
            "${sampleDateTime.format(formatterDate)}\n${sampleDateTime.format(formatterTime)}",
            dateTimeString
        )
    }

    @Test
    fun timeString() {
        val timeString = sampleDateTime.toLocalTime().timeString()
        val formatterTime = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        assertEquals(sampleDateTime.format(formatterTime), timeString)
    }

    @Test
    fun dateString() {
        val timeString = sampleDateTime.toLocalDate().dateString()
        val formatterDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
        assertEquals(sampleDateTime.format(formatterDate), timeString)
    }

    @Test
    fun lastSevenDays() {
        val sevenDaysList = lastSevenDays(sampleDateTime.toLocalDate())
        val expectedSevenDays = listOf("Thu", "Fri", "Sat", "Sun", "Mon", "Tue", "Wed")
        assertEquals(expectedSevenDays, sevenDaysList)
    }

    @Test
    fun getUtcZoneId() {
        assertEquals(ZoneId.of("UTC"), utcZoneId)
    }
}