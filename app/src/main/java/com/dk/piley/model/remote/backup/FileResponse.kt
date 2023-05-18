package com.dk.piley.model.remote.backup

import org.threeten.bp.Instant
import java.io.File

data class FileResponse(
    val file: File,
    val lastModified: Instant
)
