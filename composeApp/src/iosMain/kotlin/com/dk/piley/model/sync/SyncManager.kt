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

    // Store references to resolve delegates to prevent premature garbage collection
    private val resolveDelegates = mutableListOf<ServiceResolveDelegate>()

    override suspend fun startDiscovery(onDeviceFound: (ip: String, port: Int, timeStamp: Long) -> Unit) {
        withContext(Dispatchers.Main) {
            // Clear any previous delegates
            resolveDelegates.clear()

            browserDelegate = BrowserDelegate(onDeviceFound)
            netServiceBrowser = NSNetServiceBrowser().apply {
                setDelegate(browserDelegate)
                searchForServicesOfType(mobileSyncServiceType, "local.")
            }
        }
    }

    override suspend fun stopDiscovery() {
        withContext(Dispatchers.Main) {
            netServiceBrowser?.stop()
            netServiceBrowser?.setDelegate(null)
            netServiceBrowser = null
            browserDelegate = null
            resolveDelegates.clear()
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
                type = mobileSyncServiceType,
                name = serviceName,
                port = port
            ).apply {
                setDelegate(serviceDelegate)
                setTXTRecordData(txtRecordData)
                publish()
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
            println("Found service: ${didFindService.name} of type ${didFindService.type}")

            if (didFindService.name.startsWith(syncServiceName) &&
                !didFindService.name.contains(appPlatform.toString())
            ) {
                // Resolve this service to get its details
                val resolveDelegate = ServiceResolveDelegate(onDeviceFound)
                resolveDelegates.add(resolveDelegate) // Store reference to prevent GC
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
            if (addresses == null || addresses.isEmpty()) {
                println("No addresses found for service ${sender.name}")
                return
            }

            // Try to extract IP address from each address until we succeed
            var ipAddress: String? = null
            for (addressData in addresses) {
                ipAddress = extractIPAddress(addressData as NSData)
                if (ipAddress != null) break
            }

            if (ipAddress == null) {
                println("Failed to extract IP address for service ${sender.name}")
                return
            }

            // Get port
            val port = sender.port.toInt()
            if (port <= 0) {
                println("Invalid port number: $port")
                return
            }

            // Get timestamp from TXT record
            val txtRecordData = sender.TXTRecordData()
            if (txtRecordData == null) {
                println("No TXT record data for service ${sender.name}")
                // Still call callback but with default timestamp
                onDeviceFound(ipAddress, port, 0L)
                return
            }

            val txtDict = NSNetService.dictionaryFromTXTRecordData(txtRecordData)

            val timestampData = txtDict[timeStampAttribute] as? NSData
            val timeStamp = if (timestampData != null) {
                val timestampStr = NSString.create(timestampData, NSUTF8StringEncoding).toString()
                timestampStr.toLongOrNull() ?: 0L
            } else {
                0L
            }

            println("Successfully resolved service: $ipAddress:$port (timestamp: $timeStamp)")

            // Call the callback
            onDeviceFound(ipAddress, port, timeStamp)

            // Clean up this delegate
            sender.setDelegate(null)
            resolveDelegates.remove(this)
        }

        override fun netService(sender: NSNetService, didNotResolve: Map<Any?, *>) {
            println("Service did not resolve: $didNotResolve")
            // Clean up this delegate
            sender.setDelegate(null)
            resolveDelegates.remove(this)
        }

        @OptIn(ExperimentalForeignApi::class)
        private fun extractIPAddress(addressData: NSData): String? {
            memScoped {
                val sockaddrPtr = addressData.bytes?.reinterpret<sockaddr>()
                if (sockaddrPtr != null) {
                    val family = sockaddrPtr.pointed.sa_family.toInt()

                    return when (family) {
                        AF_INET -> { // IPv4
                            val sockaddrInPtr = sockaddrPtr.reinterpret<sockaddr_in>()
                            val addr = sockaddrInPtr.pointed.sin_addr

                            val buffer = allocArray<ByteVar>(INET_ADDRSTRLEN)
                            inet_ntop(AF_INET, addr.ptr, buffer, INET_ADDRSTRLEN.toUInt())
                            buffer.toKString()
                        }

                        AF_INET6 -> { // IPv6
                            val sockaddrIn6Ptr = sockaddrPtr.reinterpret<sockaddr_in6>()
                            val addr6 = sockaddrIn6Ptr.pointed.sin6_addr

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
            println("Service published successfully: ${sender.name} on port ${sender.port}")
        }

        override fun netService(sender: NSNetService, didNotPublish: Map<Any?, *>) {
            println("Service did not publish: $didNotPublish")
            // You might want to try again with a different approach
            // For example, if using NSNetServiceNoAutoRename failed, try publishing with auto-rename
            if (sender == netService) {
                println("Attempting to publish with auto-rename...")
                sender.publish()
            }
        }

        override fun netServiceDidStop(sender: NSNetService) {
            println("Service stopped: ${sender.name}")
        }
    }
}