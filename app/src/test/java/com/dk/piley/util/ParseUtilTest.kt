package com.dk.piley.util

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Instant

class ParseUtilTest {

    @Before
    fun setup() {

    }

    @Test
    fun toLocalDateTime() {
        val instant = Instant.parse("2023-08-14T12:02:02.000Z")
        val dateTime = instant.toLocalDateTime()
        assertEquals(dateTime.toLocalDate().toString(), "2023-08-14")
    }

    @Test
    fun toInstant() {
    }

    @Test
    fun dateTimeString() {
    }

    @Test
    fun dateTimeStringNewLine() {
    }

    @Test
    fun timeString() {
    }

    @Test
    fun dateString() {
    }

    @Test
    fun lastSevenDays() {
    }

    @Test
    fun getUtcZoneId() {
    }
}