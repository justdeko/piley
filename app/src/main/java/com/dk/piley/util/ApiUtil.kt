package com.dk.piley.util

import com.dk.piley.model.remote.backup.ContentDispositionHeaders
import okhttp3.Credentials
import okhttp3.Headers
import org.threeten.bp.Instant
import org.threeten.bp.format.DateTimeParseException
import timber.log.Timber

fun credentials(username: String?, password: String?) =
    Credentials.basic(username ?: "", password ?: "")

fun Headers.contentDispositionHeaders(): ContentDispositionHeaders? {
    val contentDisposition = this["Content-Disposition"]
    if (contentDisposition != null) {
        val fileName = contentDisposition.substringAfter("filename=").substringBefore(";", "")
        val modificationDateString = contentDisposition.substringAfter("modification-date=")
        // todo split string by ; and then search, this way it is more consistent
        Timber.d("content disposition data. filename: $fileName modification date: $modificationDateString")

        val modifiedInstant = try {
            Instant.parse(modificationDateString)
        } catch (e: DateTimeParseException) {
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