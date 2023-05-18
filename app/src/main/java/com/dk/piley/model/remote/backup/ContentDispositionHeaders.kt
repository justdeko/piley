package com.dk.piley.model.remote.backup

import org.threeten.bp.Instant

data class ContentDispositionHeaders(
    val filename: String?,
    val lastModified: Instant?
)
