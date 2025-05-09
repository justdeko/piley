package com.dk.piley.model.sync

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import com.dk.piley.model.sync.model.SyncDevice
import com.dk.piley.util.appPlatform

class SyncManager(private val context: Context) : ISyncManager {
    private lateinit var nsdManager: NsdManager
    private var registrationListener: NsdManager.RegistrationListener? = null
    private var discoveryListener: NsdManager.DiscoveryListener? = null


    override suspend fun startDiscovery(onDeviceFound: (SyncDevice) -> Unit) {
        nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager

        discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(serviceType: String) {}
            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                if (serviceInfo.serviceName.startsWith(syncServiceName)
                    && !serviceInfo.serviceName.contains(appPlatform.toString())
                ) {
                    nsdManager.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                        override fun onServiceResolved(resolvedInfo: NsdServiceInfo) {
                            val name = resolvedInfo.serviceName
                            println("Service resolved: $name}")
                            val host = resolvedInfo.host.hostName
                            val port = resolvedInfo.port
                            val timeStamp =
                                resolvedInfo.attributes[timeStampAttribute]?.let { String(it) }
                                    ?.toLongOrNull() ?: 0L
                            if (!resolvedInfo.serviceName.contains(appPlatform.toString())) {
                                onDeviceFound(
                                    SyncDevice(
                                        name = name,
                                        hostName = host,
                                        port = port,
                                        lastModifiedTimestamp = timeStamp
                                    )
                                )
                            }
                        }

                        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}
                    })
                }
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo) {}
            override fun onDiscoveryStopped(serviceType: String) {}
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {}
            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {}
        }

        nsdManager.discoverServices(
            /* serviceType = */ mobileSyncServiceType,
            /* protocolType = */ NsdManager.PROTOCOL_DNS_SD,
            /* listener = */ discoveryListener
        )
    }

    override suspend fun stopDiscovery() {
        discoveryListener?.let { nsdManager.stopServiceDiscovery(it) }
    }

    override suspend fun advertiseService(port: Int, timeStamp: Long) {
        nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
        val serviceInfo = NsdServiceInfo().apply {
            serviceName = syncServiceName + appPlatform
            serviceType = mobileSyncServiceType
            setAttribute(timeStampAttribute, timeStamp.toString())
            setPort(port)
        }

        registrationListener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(serviceInfo: NsdServiceInfo) {}
            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}
            override fun onServiceUnregistered(serviceInfo: NsdServiceInfo) {}
            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}
        }

        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
    }

    override suspend fun stopAdvertising() {
        registrationListener?.let { nsdManager.unregisterService(it) }
    }
}