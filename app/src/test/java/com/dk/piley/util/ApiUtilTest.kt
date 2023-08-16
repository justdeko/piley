package com.dk.piley.util

import okhttp3.Credentials
import okhttp3.Headers
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class ApiUtilTest {

    @Test
    fun testCredentials() {
        val credentials = credentials("username", "password")
        val expectedCredentials = Credentials.basic("username", "password")
        assertEquals(expectedCredentials, credentials)
    }

    @Test
    fun testContentDispositionHeaders() {
        val headers = Headers.headersOf(
            "Content-Disposition",
            "attachment; filename=backupfilename.db; modification-date=\"2023-08-14T15:58:15Z\""
        )
        val contentHeaders = headers.contentDispositionHeaders()
        assertEquals("backupfilename.db", contentHeaders?.filename)
        assertEquals(Instant.parse("2023-08-14T15:58:15Z"), contentHeaders?.lastModified)
    }
}