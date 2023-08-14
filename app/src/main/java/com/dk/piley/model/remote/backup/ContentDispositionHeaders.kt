package com.dk.piley.model.remote.backup

import java.time.Instant

data class ContentDispositionHeaders(
    val filename: String?,
    val lastModified: Instant?
)
