package com.dk.piley.model.remote.backup

import java.io.File
import java.time.Instant

/**
 * File response sent to the api when doing backups
 *
 * @property file the file entity containing the backup
 * @property lastModified the last modification date of the backup
 */
data class FileResponse(
    val file: File,
    val lastModified: Instant
)
