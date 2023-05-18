package com.dk.piley.util

import com.dk.piley.model.remote.backup.ContentDispositionHeaders
import okhttp3.Credentials
import okhttp3.Headers
import org.threeten.bp.Instant
import org.threeten.bp.format.DateTimeParseException

fun credentials(username: String?, password: String?) =
    Credentials.basic(username ?: "", password ?: "")

fun Headers.contentDispositionHeaders(): ContentDispositionHeaders? {
    val contentDisposition = this["Content-Disposition"]
    if (contentDisposition != null) {
        val fileNameRegex = Regex("filename=['\"]?([^'\"\\s]+)['\"]?")
        val fileNameMatchResult = fileNameRegex.find(contentDisposition)
        val fileName = fileNameMatchResult?.groupValues?.get(1)

        val modifiedRegex = Regex("modification-date=['\"]?([^'\"\\s]+)['\"]?")
        val modifiedMatchResult = modifiedRegex.find(contentDisposition)
        val modifiedDateString = modifiedMatchResult?.groupValues?.get(1)
        val modifiedInstant = try {
            Instant.parse(modifiedDateString)
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