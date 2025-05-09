package com.dk.piley.model.sync

import com.dk.piley.model.sync.model.SyncDevice
import com.dk.piley.util.appPlatform
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
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class SyncManager : ISyncManager {
    private var netServiceBrowser: NSNetServiceBrowser? = null
    private var netService: NSNetService? = null
    private var serviceDelegate: ServiceDelegate? = null
    private var browserDelegate: BrowserDelegate? = null

    private val resolveDelegates = mutableListOf<ServiceResolveDelegate>()

    override suspend fun startDiscovery(onDeviceFound: (SyncDevice) -> Unit) {
        withContext(Dispatchers.Main) {
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

            val txtRecordDict = mutableMapOf<String, String>()
            txtRecordDict[timeStampAttribute] = timeStamp.toString()
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
        private val onDeviceFound: (SyncDevice) -> Unit
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
                val resolveDelegate = ServiceResolveDelegate(onDeviceFound)
                resolveDelegates.add(resolveDelegate)
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

    private inner class ServiceResolveDelegate(
        private val onDeviceFound: (SyncDevice) -> Unit
    ) : NSObject(), NSNetServiceDelegateProtocol {

        override fun netServiceDidResolveAddress(sender: NSNetService) {
            val hostName = sender.hostName
            if (hostName == null) {
                println("Failed to resolve host name for service ${sender.name}")
                return
            }

            val port = sender.port.toInt()
            if (port <= 0) {
                println("Invalid port number: $port")
                return
            }

            val txtRecordData = sender.TXTRecordData()
            val syncDevice = SyncDevice(
                name = sender.name,
                hostName = hostName,
                port = port,
            )
            if (txtRecordData == null) {
                println("No TXT record data for service ${sender.name}")
                onDeviceFound(syncDevice)
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

            println("Successfully resolved service: $hostName:$port (timestamp: $timeStamp)")
            onDeviceFound(syncDevice.copy(lastModifiedTimestamp = timeStamp))
            sender.setDelegate(null)
            resolveDelegates.remove(this)
        }

        override fun netService(sender: NSNetService, didNotResolve: Map<Any?, *>) {
            println("Service did not resolve: $didNotResolve")
            sender.setDelegate(null)
            resolveDelegates.remove(this)
        }
    }

    private inner class ServiceDelegate : NSObject(), NSNetServiceDelegateProtocol {
        override fun netServiceDidPublish(sender: NSNetService) {
            println("Service published successfully: ${sender.name} on port ${sender.port}")
        }

        override fun netService(sender: NSNetService, didNotPublish: Map<Any?, *>) {
            println("Service did not publish: $didNotPublish")
        }

        override fun netServiceDidStop(sender: NSNetService) {
            println("Service stopped: ${sender.name}")
        }
    }
}