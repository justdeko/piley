package com.dk.piley.model.sync

interface ISyncManager {
    suspend fun startDiscovery(onDeviceFound: (ip: String, port: Int) -> Unit)
    suspend fun stopDiscovery()
    suspend fun advertiseService(port: Int)
    suspend fun stopAdvertising()
}

const val syncServiceType = "_piley._tcp."
const val syncServiceName = "Piley"