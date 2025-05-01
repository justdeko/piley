package com.dk.piley.model.sync

import com.dk.piley.util.appPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SyncCoordinator(
    private val syncManager: ISyncManager
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val _syncState = MutableStateFlow(SyncState.Idle)
    val syncStateFlow: StateFlow<SyncState> = _syncState

    private lateinit var client: Job
    private lateinit var server: Job

    private companion object {
        const val PORT = 55122
    }

    fun startSync(
        lastEditedTimeStamp: Long,
    ) {
        println("Starting sync process with timestamp $lastEditedTimeStamp")

        _syncState.value = SyncState.Discovering

        server = scope.launch {
            startServer(PORT) { data ->
                println("Received data: ${data.decodeToString()}")
                stopSync()
            }
        }

        client = scope.launch {
            syncManager.advertiseService(PORT, lastEditedTimeStamp)
            _syncState.value = SyncState.Advertising
            syncManager.startDiscovery { ip, port, remoteTimestamp ->
                println("Found device at $ip:$port (timestamp=$remoteTimestamp)")
                scope.launch {
                    try {
                        _syncState.value = SyncState.Syncing
                        if (lastEditedTimeStamp > remoteTimestamp) {
                            val data = "Hello from device $appPlatform!".encodeToByteArray()
                            sendData(ip, port, data)
                            println("Data sent to $ip")
                            _syncState.value = SyncState.Synced
                        } else {
                            println("Remote device is newer → waiting for incoming data")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        syncManager.stopDiscovery()
                        syncManager.stopAdvertising()
                        _syncState.value = SyncState.Error
                    }
                }
            }
        }
    }


    suspend fun stopSync() {
        withContext(Dispatchers.IO) {
            syncManager.stopDiscovery()
            syncManager.stopAdvertising()
            client.cancel()
            server.cancel()
            _syncState.value = SyncState.Idle
        }
    }
}

enum class SyncState {
    Idle,
    Advertising,
    Discovering,
    Syncing,
    Synced,
    Error
}
