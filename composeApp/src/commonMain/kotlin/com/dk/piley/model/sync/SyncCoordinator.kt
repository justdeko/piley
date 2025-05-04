package com.dk.piley.model.sync

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.ServerSocket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.readFully
import io.ktor.utils.io.readLong
import io.ktor.utils.io.writeFully
import io.ktor.utils.io.writeLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class SyncCoordinator(
    private val syncManager: ISyncManager
) {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val _syncState = MutableStateFlow(SyncState.Idle)
    val syncStateFlow: StateFlow<SyncState> = _syncState

    private var selector: SelectorManager? = null
    private var serverSocket: ServerSocket? = null

    private companion object {
        const val PORT = 55122
    }

    fun startSync(
        lastEditedTimeStamp: Long,
        dataToSend: ByteArray,
        onFileReceived: (ByteArray) -> Unit
    ) {
        println("Starting sync process with timestamp $lastEditedTimeStamp")
        _syncState.value = SyncState.Discovering

        scope.launch {
            startServer(PORT) { data ->
                println("Received data: ${data.size} bytes")
                onFileReceived(data)
                _syncState.value = SyncState.Synced
                stopSync()
            }
        }

        scope.launch {
            try {
                syncManager.advertiseService(PORT, lastEditedTimeStamp)
                _syncState.value = SyncState.Advertising

                syncManager.startDiscovery { hostName, port, remoteTimestamp ->
                    println("Found device at $hostName:$port (timestamp=$remoteTimestamp)")
                    scope.launch {
                        try {
                            _syncState.value = SyncState.Syncing
                            if (lastEditedTimeStamp > remoteTimestamp) {
                                sendData(hostName, port, dataToSend)
                                println("Data sent to $hostName")
                                _syncState.value = SyncState.Synced
                            } else {
                                println("Remote device is newer → waiting for incoming data")
                            }
                        } catch (e: Exception) {
                            println("Error during client send: ${e.message}")
                            e.printStackTrace()
                            _syncState.value = SyncState.Error
                        } finally {
                            scope.launch { stopSync() }
                        }
                    }
                }
            } catch (e: Exception) {
                println("Discovery error: ${e.message}")
                _syncState.value = SyncState.Error
                stopSync()
            }
        }
    }

    private suspend fun startServer(
        port: Int,
        onReceive: suspend (ByteArray) -> Unit
    ) {
        selector = SelectorManager(Dispatchers.IO)
        serverSocket = aSocket(selector!!).tcp().bind("0.0.0.0", port)
        println("Server started on port $port")

        try {
            while (scope.isActive) {
                val socket = serverSocket?.accept() ?: break
                scope.launch {
                    try {
                        val input = socket.openReadChannel()
                        val fileSize = input.readLong()
                        val buffer = ByteArray(fileSize.toInt())
                        input.readFully(buffer, 0, buffer.size)
                        onReceive(buffer)
                    } catch (e: Exception) {
                        if (e !is CancellationException) {
                            println("Server socket error: ${e::class.simpleName}: ${e.message}")
                        }
                    } finally {
                        socket.close()
                        println("Socket closed after receiving data.")
                    }
                }
            }
        } catch (e: Exception) {
            val name = e::class.simpleName ?: e.toString()
            if (name.contains(
                    "ClosedChannelException",
                    ignoreCase = true
                ) || e is CancellationException
            ) {
                println("Server socket closed.")
            } else {
                println("Server loop error: $name: ${e.message ?: "no message"}")
            }
        } finally {
            println("Server shutting down.")
            serverSocket?.close()
            selector?.close()
        }
    }

    private suspend fun sendData(hostName: String, port: Int, data: ByteArray) {
        val selector = SelectorManager(Dispatchers.IO)
        try {
            val socket = aSocket(selector).tcp().connect(hostName, port)
            val output = socket.openWriteChannel(autoFlush = true)

            output.writeLong(data.size.toLong())
            output.writeFully(data)
            println("Sent ${data.size} bytes to $hostName:$port")

            socket.close()
        } catch (e: Exception) {
            println("sendData error: ${e.message}")
            throw e
        } finally {
            selector.close()
        }
    }

    suspend fun stopSync() {
        println("Stopping sync...")
        withContext(Dispatchers.IO) {
            runCatching { syncManager.stopDiscovery() }
            runCatching { syncManager.stopAdvertising() }
            runCatching { serverSocket?.close() }
            runCatching { selector?.close() }
            job.cancelChildren()
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
