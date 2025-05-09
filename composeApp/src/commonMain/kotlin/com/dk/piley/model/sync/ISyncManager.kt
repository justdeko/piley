package com.dk.piley.model.sync

import com.dk.piley.model.sync.model.SyncDevice

interface ISyncManager {
    suspend fun startDiscovery(onDeviceFound: (SyncDevice) -> Unit)
    suspend fun stopDiscovery()
    suspend fun advertiseService(port: Int, timeStamp: Long)
    suspend fun stopAdvertising()
}

const val syncServiceType = "_piley._tcp.local."
const val mobileSyncServiceType = "_piley._tcp"
const val syncServiceName = "Piley_"
const val timeStampAttribute = "timestamp"