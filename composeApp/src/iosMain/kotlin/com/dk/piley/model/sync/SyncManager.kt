package com.dk.piley.model.sync

import com.dk.piley.util.appPlatform
import io.github.vinceglb.filekit.utils.toByteArray
import io.github.vinceglb.filekit.utils.toNSData
import io.ktor.utils.io.core.toByteArray
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.pointed
import kotlinx.cinterop.reinterpret
import platform.Foundation.NSNetServiceBrowser
import platform.Foundation.NSNetServiceBrowserDelegateProtocol
import platform.Foundation.NSNetServiceDelegateProtocol
import platform.darwin.NSObject

class SyncManager : ISyncManager {
    private var netService: platform.Foundation.NSNetService? = null
    private var serviceBrowser: NSNetServiceBrowser? = null

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun startDiscovery(onDeviceFound: (ip: String, port: Int, timeStamp: Long) -> Unit) {
        serviceBrowser = NSNetServiceBrowser()
        serviceBrowser!!.delegate =
            object : NSObject(), NSNetServiceBrowserDelegateProtocol {
                override fun netServiceBrowser(
                    browser: NSNetServiceBrowser,
                    didFindService: platform.Foundation.NSNetService,
                    moreComing: Boolean
                ) {
                    didFindService.delegate =
                        object : NSObject(), NSNetServiceDelegateProtocol {
                            override fun netServiceDidResolveAddress(sender: platform.Foundation.NSNetService) {
                                sender.addresses?.firstOrNull()?.let {
                                    val data = it as platform.Foundation.NSData
                                    val socketAddressPointer =
                                        data.bytes?.reinterpret<platform.posix.sockaddr_in>()
                                    val ipBytes =
                                        socketAddressPointer?.pointed?.sin_addr?.s_addr ?: return
                                    val ip = ipBytes.toLong() and 0xFF or
                                            ((ipBytes.toLong() shr 8) and 0xFF) shl 8 or
                                            ((ipBytes.toLong() shr 16) and 0xFF) shl 16 or
                                            ((ipBytes.toLong() shr 24) and 0xFF) shl 24
                                    val port = sender.port.toInt()
                                    val recordData = sender.TXTRecordData()
                                    val recordString = recordData?.toByteArray()?.decodeToString()
                                    val name = sender.name
                                    val timeStamp =
                                        recordString?.substringAfter("$timeStampAttribute=")
                                            ?.toLongOrNull() ?: 0L
                                    if (!name.contains(appPlatform.toString())) {
                                        onDeviceFound(ip.toString(), port, timeStamp)
                                    }
                                }
                            }
                        }
                    didFindService.resolveWithTimeout(5.0)
                }
            }
        serviceBrowser?.searchForServicesOfType(syncServiceType, inDomain = "local.")
    }

    override suspend fun stopDiscovery() {
        serviceBrowser?.stop()
    }

    override suspend fun advertiseService(port: Int, timeStamp: Long) {
        netService = platform.Foundation.NSNetService(
            domain = "local.",
            type = syncServiceType,
            name = syncServiceName + appPlatform,
            port = port
        )
        val txtRecord = "$timeStampAttribute=$timeStamp"
        val txtData = txtRecord.toByteArray().toNSData()
        netService?.setTXTRecordData(txtData)
        netService?.scheduleInRunLoop(
            platform.Foundation.NSRunLoop.currentRunLoop,
            platform.Foundation.NSDefaultRunLoopMode
        )
        netService?.publish()
    }

    override suspend fun stopAdvertising() {
        netService?.stop()
    }
}