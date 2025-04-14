package com.dk.piley.model.sync

interface SyncManager {
    suspend fun startDiscovery(onDeviceFound: (ip: String, port: Int) -> Unit)
    suspend fun stopDiscovery()
    suspend fun advertiseService(port: Int)
    suspend fun startServer(port: Int, onReceive: suspend (ByteArray) -> Unit)
}