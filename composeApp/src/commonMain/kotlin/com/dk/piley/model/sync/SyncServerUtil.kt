package com.dk.piley.model.sync

import io.ktor.network.selector.SelectorManager
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
import kotlinx.coroutines.launch

suspend fun startServer(
    port: Int,
    lastEdited: Long,
    onReceive: suspend (ByteArray) -> Unit
) {
    val selector = SelectorManager(Dispatchers.IO)
    val serverSocket = aSocket(selector).tcp().bind("0.0.0.0", port)
    println("Server started on port $port")

    while (true) {
        val socket = serverSocket.accept()
        CoroutineScope(Dispatchers.IO).launch {
            val input = socket.openReadChannel()
            val output = socket.openWriteChannel(autoFlush = true)

            val remoteTimestamp = input.readLong()
            output.writeLong(lastEdited)

            println("(startServer) Local timestamp: $lastEdited, Remote timestamp: $remoteTimestamp")

            if (remoteTimestamp < lastEdited) {
                println("Local device is newer, expecting file...")
                val fileSize = input.readLong()
                val buffer = ByteArray(fileSize.toInt())
                input.readFully(buffer, 0, buffer.size)
                onReceive(buffer)
                socket.close()
                println("File received, closing socket.")
            } else {
                println("Remote device is newer, no file transfer needed.")
            }
        }
    }
}

suspend fun handshakeAndMaybeSync(
    ip: String,
    port: Int,
    localTimestamp: Long,
    localData: ByteArray
) {
    val selector = SelectorManager(Dispatchers.IO)
    val socket = aSocket(selector).tcp().connect(ip, port)
    val input = socket.openReadChannel()
    val output = socket.openWriteChannel(autoFlush = true)

    output.writeLong(localTimestamp)
    val remoteTimestamp = input.readLong()

    println("(handshake) Local timestamp: $localTimestamp, Remote timestamp: $remoteTimestamp")

    if (localTimestamp < remoteTimestamp) {
        println("Remote device is newer, sending file...")
        output.writeLong(localData.size.toLong())
        output.writeFully(localData)
        socket.close()
        println("File sent, closing socket.")
    } else {
        println("Local device is newer, no file sent, expecting file...")
    }
}