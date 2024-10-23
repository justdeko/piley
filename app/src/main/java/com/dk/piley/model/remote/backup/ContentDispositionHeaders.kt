package com.dk.piley.model.remote.backup

import kotlinx.datetime.Instant

/**
 * Content disposition headers returned from an api response of a backup
 *
 * @property filename the backup file name
 * @property lastModified last modification date of the backup
 */
data class ContentDispositionHeaders(
    val filename: String?,
    val lastModified: Instant?
)
