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
    val lastSynced: Long = 0L,
)

fun List<SyncDevice>.toJsonString(): String = Json.encodeToString(this)
fun toSyncDeviceList(jsonString: String): List<SyncDevice> = try {
    Json.decodeFromString(jsonString)
} catch (e: Exception) {
    println("Error decoding SyncDevice list: ${e.message}")
    emptyList()
}
