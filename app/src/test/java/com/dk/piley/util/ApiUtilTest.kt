package com.dk.piley.util

import okhttp3.Credentials
import okhttp3.Headers
import org.junit.Assert.assertEquals
import org.junit.Test

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
            "form-data; modification-date==\"fieldName\"; filename=\"filename.db\""
        )
        val contentHeaders = headers.contentDispositionHeaders()
    }
}