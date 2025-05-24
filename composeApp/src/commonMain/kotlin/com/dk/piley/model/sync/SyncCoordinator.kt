package com.dk.piley.model.sync

import com.dk.piley.model.sync.model.SyncDevice
import com.dk.piley.model.user.UserPrefsManager
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlin.coroutines.cancellation.CancellationException

class SyncCoordinator(
    private val syncManager: ISyncManager,
    private val userPrefsManager: UserPrefsManager,
) {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private var selector: SelectorManager? = null
    private var serverSocket: ServerSocket? = null

    private companion object {
        const val PORT = 55122
    }

    fun startDiscovery(onDeviceFound: (SyncDevice) -> Unit) {
        scope.launch {
            try {
                syncManager.startDiscovery { syncDevice ->
                    println("Found device at ${syncDevice.hostName}:${syncDevice.port} (lastSynced=${syncDevice.lastSynced})")
                    onDeviceFound(syncDevice)
                }
            } catch (e: Exception) {
                println("Discovery error: ${e.message}")
            }
        }
    }

    fun startAdvertising() {
        scope.launch {
            try {
                val lastSynced = userPrefsManager.getLastSynced().firstOrNull() ?: 0L
                syncManager.advertiseService(PORT, lastSynced)
            } catch (e: Exception) {
                println("Advertising error: ${e.message}")
            }
        }
    }

    suspend fun getStoredServices(): List<SyncDevice> {
        return userPrefsManager.getStoredServices().firstOrNull() ?: emptyList()
    }

    suspend fun saveServices(
        services: List<SyncDevice>
    ) {
        userPrefsManager.setStoredServices(services)
    }

    suspend fun startServer(
        port: Int = PORT,
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
                        println("Receiving $fileSize bytes from ${socket.remoteAddress}")
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

    suspend fun sendData(
        hostName: String,
        data: ByteArray,
        port: Int = PORT,
    ) {
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

    fun setLastSynced() {
        scope.launch {
            userPrefsManager.setLastSynced(Clock.System.now().toEpochMilliseconds())
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
        }
    }

    fun stopServer() {
        println("Stopping server...")
        runCatching { serverSocket?.close() }
        runCatching { selector?.close() }
    }
}
