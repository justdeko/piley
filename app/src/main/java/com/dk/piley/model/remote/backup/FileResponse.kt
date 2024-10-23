package com.dk.piley.model.remote.backup

import com.google.gson.annotations.SerializedName
import kotlinx.datetime.Instant
import java.io.File

/**
 * File response sent to the api when doing backups
 *
 * @property file the file entity containing the backup
 * @property lastModified the last modification date of the backup
 */
data class FileResponse(
    @SerializedName("file") val file: File,
    @SerializedName("lastModified") val lastModified: Instant
)
