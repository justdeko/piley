package com.dk.piley.model.sync

import com.dk.piley.util.appPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SyncCoordinator(
    private val syncManager: ISyncManager
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val _syncState = MutableStateFlow(SyncState.Idle)
    val syncStateFlow: StateFlow<SyncState> = _syncState

    private lateinit var server: Job
    private lateinit var client: Job

    private companion object {
        const val PORT = 55122
    }

    fun startSync(
        lastEditedTimeStamp: Long,
    ) {
        _syncState.value = SyncState.Advertising
        server = scope.launch {
            syncManager.advertiseService(PORT)
            startServer(
                port = PORT,
                lastEdited = lastEditedTimeStamp,
                onReceive = { receivedBytes ->
                    println("Received mock file: ${receivedBytes.decodeToString()}")
                    _syncState.value = SyncState.Synced
                    client.cancel()
                }
            )
        }
        _syncState.value = SyncState.Discovering
        // TODO: send from desktop to mobile not working, timestamp identical (maybe connecting to itself?)
        // maybe use handshake + advertise service?
        client = scope.launch {
            println("Starting client...")
            syncManager.startDiscovery { ip, port ->
                println("Found device at $ip:$port")
                scope.launch {
                    try {
                        _syncState.value = SyncState.Syncing
                        val mockData = "Hello from device $appPlatform!".encodeToByteArray()
                        handshakeAndMaybeSync(ip, port, lastEditedTimeStamp, mockData)
                        _syncState.value = SyncState.Synced
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _syncState.value = SyncState.Error
                    }
                    server.cancel()
                }
            }
        }
        server.start()
        client.start()
    }

    suspend fun stopSync() {
        syncManager.stopDiscovery()
        syncManager.stopAdvertising()
        server.cancel()
        client.cancel()
        _syncState.value = SyncState.Idle
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
