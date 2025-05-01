package com.dk.piley.model.sync

interface ISyncManager {
    suspend fun startDiscovery(onDeviceFound: (ip: String, port: Int, timeStamp: Long) -> Unit)
    suspend fun stopDiscovery()
    suspend fun advertiseService(port: Int, timeStamp: Long)
    suspend fun stopAdvertising()
}

const val syncServiceType = "_piley._tcp.local."
const val syncServiceName = "Piley_"
const val timeStampAttribute = "timestamp"