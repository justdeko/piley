package com.dk.piley.model.remote.backup

import java.io.File
import java.time.Instant

data class FileResponse(
    val file: File,
    val lastModified: Instant
)
