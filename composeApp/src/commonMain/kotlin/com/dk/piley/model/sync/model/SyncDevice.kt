package com.dk.piley.model.sync.model

import com.dk.piley.util.Platform
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class SyncDevice(
    val name: String,
    val platform: Platform = Platform.fromValue(name.split("_").last()),
    val hostName: String,
    val port: Int,
    val lastModifiedTimestamp: Long = 0L,
)

fun List<SyncDevice>.toJsonString(): String = Json.encodeToString(this)
fun toSyncDeviceList(jsonString: String): List<SyncDevice> = Json.decodeFromString(jsonString)
