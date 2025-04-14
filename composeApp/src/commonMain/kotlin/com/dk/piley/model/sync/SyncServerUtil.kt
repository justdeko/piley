package com.dk.piley.model.sync

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.utils.io.readFully
import io.ktor.utils.io.readLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

suspend fun startServer(
    port: Int,
    onReceive: suspend (ByteArray) -> Unit
) {
    val selector = SelectorManager(Dispatchers.IO)
    val serverSocket = aSocket(selector).tcp().bind("0.0.0.0", port)
    println("Server started on port $port")

    while (true) {
        val socket = serverSocket.accept()
        CoroutineScope(Dispatchers.IO).launch {
            val input = socket.openReadChannel()
            val fileSize = input.readLong()
            val buffer = ByteArray(fileSize.toInt())
            input.readFully(buffer, 0, buffer.size)
            onReceive(buffer)
            socket.close()
        }
    }
}