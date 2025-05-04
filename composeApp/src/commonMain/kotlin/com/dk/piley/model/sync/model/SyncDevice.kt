package com.dk.piley.model.sync.model

import com.dk.piley.util.Platform

data class SyncDevice(
    val platform: Platform,
    val name: String,
)
