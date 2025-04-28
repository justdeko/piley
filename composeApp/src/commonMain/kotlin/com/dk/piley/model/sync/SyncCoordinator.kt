package com.dk.piley.model.sync

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    private companion object {
        const val PORT = 55122
    }

    fun startSync(
        lastEditedTimeStamp: Long,
    ) {
        _syncState.value = SyncState.Advertising
        scope.launch {
            val mockData = "Hello from device!".encodeToByteArray()

            syncManager.advertiseService(PORT)

            startServer(
                port = PORT,
                lastEdited = lastEditedTimeStamp,
                onReceive = { receivedBytes ->
                    println("Received mock file: ${receivedBytes.decodeToString()}")
                    _syncState.value = SyncState.Synced
                }
            )
        }

        _syncState.value = SyncState.Discovering
        scope.launch {
            syncManager.startDiscovery { ip, port ->
                scope.launch {
                    try {
                        _syncState.value = SyncState.Syncing
                        val mockData = "Hello from device!".encodeToByteArray()
                        handshakeAndMaybeSync(ip, port, lastEditedTimeStamp, mockData)
                        _syncState.value = SyncState.Synced
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _syncState.value = SyncState.Error
                    }
                }
            }
        }
    }

    suspend fun stopSync() {
        syncManager.stopDiscovery()
        syncManager.stopAdvertising()
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
