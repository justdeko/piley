package com.dk.piley.util

import com.dk.piley.model.remote.backup.ContentDispositionHeaders
import okhttp3.Credentials
import okhttp3.Headers
import timber.log.Timber
import java.time.Instant
import java.time.format.DateTimeParseException

/**
 * Basic auth credentials for api requests
 *
 * @param username user name
 * @param password user password
 */
fun credentials(username: String?, password: String?) =
    Credentials.basic(username ?: "", password ?: "")

/**
 * Extract content disposition header values from response headers
 *
 * @return [ContentDispositionHeaders] entity containing backup file name and last modification date
 */
fun Headers.contentDispositionHeaders(): ContentDispositionHeaders? {
    val contentDisposition = this["Content-Disposition"]
    if (contentDisposition != null) {
        val entries = contentDisposition.split(";")
        val fileName = entries.find { it.contains("filename=") }?.substringAfter("=")
        val modificationDateString =
            entries.find { it.contains("modification-date=") }
                ?.substringAfter("=")
                ?.removeSurrounding("\"")
        Timber.d("content disposition data. filename: $fileName modification date: $modificationDateString")

        val modifiedInstant = try {
            Instant.parse(modificationDateString)
        } catch (e: DateTimeParseException) {
            null
        } catch (e: NullPointerException) {
            null
        }
        return ContentDispositionHeaders(
            filename = fileName,
            lastModified = modifiedInstant
        )
    } else {
        return null
    }
}