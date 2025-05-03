package com.dk.piley.model.sync

import com.dk.piley.util.appPlatform
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSNetService
import platform.Foundation.NSNetServiceBrowser
import platform.Foundation.NSNetServiceBrowserDelegateProtocol
import platform.Foundation.NSNetServiceDelegateProtocol
import platform.Foundation.NSNetServiceNoAutoRename
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.darwin.NSObject
import platform.darwin.inet_ntop
import platform.posix.AF_INET
import platform.posix.AF_INET6
import platform.posix.INET6_ADDRSTRLEN
import platform.posix.INET_ADDRSTRLEN
import platform.posix.sockaddr
import platform.posix.sockaddr_in
import platform.posix.sockaddr_in6
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class SyncManager : ISyncManager {
    private var netServiceBrowser: NSNetServiceBrowser? = null
    private var netService: NSNetService? = null
    private var serviceDelegate: ServiceDelegate? = null
    private var browserDelegate: BrowserDelegate? = null

    override suspend fun startDiscovery(onDeviceFound: (ip: String, port: Int, timeStamp: Long) -> Unit) {
        withContext(Dispatchers.Main) {
            browserDelegate = BrowserDelegate(onDeviceFound)
            netServiceBrowser = NSNetServiceBrowser().apply {
                setDelegate(browserDelegate)
                searchForServicesOfType(syncServiceType, "local.")
            }
        }
    }

    override suspend fun stopDiscovery() {
        withContext(Dispatchers.Main) {
            netServiceBrowser?.stop()
            netServiceBrowser?.setDelegate(null)
            netServiceBrowser = null
            browserDelegate = null
        }
    }

    override suspend fun advertiseService(port: Int, timeStamp: Long) {
        withContext(Dispatchers.Main) {
            val serviceName = syncServiceName + appPlatform

            // Create a dictionary for the TXT record data
            val txtRecordDict = mutableMapOf<String, String>()
            txtRecordDict[timeStampAttribute] = timeStamp.toString()

            // Convert to NSData for TXT record
            val txtRecordData = NSNetService.dataFromTXTRecordDictionary(
                txtRecordDict.map { (key, value) ->
                    key to value.encodeToByteArray()
                }.toMap() as Map<Any?, *>
            )

            serviceDelegate = ServiceDelegate()
            netService = NSNetService(
                domain = "local.",
                type = syncServiceType,
                name = serviceName,
                port = port
            ).apply {
                setDelegate(serviceDelegate)
                setTXTRecordData(txtRecordData)
                publishWithOptions(NSNetServiceNoAutoRename)
            }
        }
    }

    override suspend fun stopAdvertising() {
        withContext(Dispatchers.Main) {
            netService?.stop()
            netService?.setDelegate(null)
            netService = null
            serviceDelegate = null
        }
    }

    private inner class BrowserDelegate(
        private val onDeviceFound: (ip: String, port: Int, timeStamp: Long) -> Unit
    ) : NSObject(), NSNetServiceBrowserDelegateProtocol {

        override fun netServiceBrowser(
            browser: NSNetServiceBrowser,
            didFindService: NSNetService,
            moreComing: Boolean
        ) {
            if (didFindService.name.startsWith(syncServiceName) &&
                !didFindService.name.contains(appPlatform.toString())
            ) {
                // Resolve this service to get its details
                val resolveDelegate = ServiceResolveDelegate(onDeviceFound)
                didFindService.setDelegate(resolveDelegate)
                didFindService.resolveWithTimeout(5.0)
            }
        }

        override fun netServiceBrowser(browser: NSNetServiceBrowser, didNotSearch: Map<Any?, *>) {
            println("Browser did not search: $didNotSearch")
        }

        override fun netServiceBrowserDidStopSearch(browser: NSNetServiceBrowser) {
            println("Browser did stop search")
        }

        override fun netServiceBrowserWillSearch(browser: NSNetServiceBrowser) {
            println("Browser will search")
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private inner class ServiceResolveDelegate(
        private val onDeviceFound: (ip: String, port: Int, timeStamp: Long) -> Unit
    ) : NSObject(), NSNetServiceDelegateProtocol {

        override fun netServiceDidResolveAddress(sender: NSNetService) {
            // Get IP address
            val addresses = sender.addresses
            if (addresses == null || addresses.isEmpty()) return

            // Extract the first IP address
            val ipAddress = extractIPAddress(addresses.first() as NSData)
            if (ipAddress == null) {
                println("Failed to extract IP address")
                return
            }

            // Get port
            val port = sender.port.toInt()

            // Get timestamp from TXT record
            val txtRecordData = sender.TXTRecordData() ?: return
            val txtDict = NSNetService.dictionaryFromTXTRecordData(txtRecordData)

            val timestampData = txtDict[timeStampAttribute] as? NSData
            val timeStamp = if (timestampData != null) {
                NSString.create(timestampData, NSUTF8StringEncoding).toString().toLongOrNull() ?: 0L
            } else {
                0L
            }

            // Call the callback
            onDeviceFound(ipAddress, port, timeStamp)
        }

        override fun netService(sender: NSNetService, didNotResolve: Map<Any?, *>) {
            println("Service did not resolve: $didNotResolve")
        }

        @OptIn(ExperimentalForeignApi::class)
        private fun extractIPAddress(addressData: NSData): String? {
            // This is a simplified approach - socket address parsing
            // For demonstration, we'll convert socket address to a string

            // In a real implementation, you'd need to inspect the socket address structure
            // and extract the IP address properly

            // First 2 bytes are address family, we need to check if IPv4 or IPv6
            memScoped {
                val sockaddrPtr = addressData.bytes?.reinterpret<sockaddr>()
                if (sockaddrPtr != null) {
                    val family = sockaddrPtr.pointed.sa_family.toInt()

                    // Check family (AF_INET = 2 for IPv4, AF_INET6 = 30 for IPv6)
                    return when (family) {
                        2 -> { // AF_INET (IPv4)
                            val sockaddrInPtr = sockaddrPtr.reinterpret<sockaddr_in>()
                            val addr = sockaddrInPtr.pointed.sin_addr

                            // Convert address to string
                            val buffer = allocArray<ByteVar>(INET_ADDRSTRLEN)
                            inet_ntop(AF_INET, addr.ptr, buffer, INET_ADDRSTRLEN.toUInt())
                            buffer.toKString()
                        }

                        30 -> { // AF_INET6 (IPv6)
                            val sockaddrIn6Ptr = sockaddrPtr.reinterpret<sockaddr_in6>()
                            val addr6 = sockaddrIn6Ptr.pointed.sin6_addr

                            // Convert address to string
                            val buffer = allocArray<ByteVar>(INET6_ADDRSTRLEN)
                            inet_ntop(AF_INET6, addr6.ptr, buffer, INET6_ADDRSTRLEN.toUInt())
                            buffer.toKString()
                        }

                        else -> null
                    }
                }
                return null
            }
        }
    }

    private inner class ServiceDelegate : NSObject(), NSNetServiceDelegateProtocol {
        override fun netServiceDidPublish(sender: NSNetService) {
            println("Service published: ${sender.name}")
        }

        override fun netService(sender: NSNetService, didNotPublish: Map<Any?, *>) {
            println("Service did not publish: $didNotPublish")
        }

        override fun netServiceDidStop(sender: NSNetService) {
            println("Service stopped: ${sender.name}")
        }
    }
}