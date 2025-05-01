package com.dk.piley.model.sync

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import com.dk.piley.util.appPlatform

class SyncManager(private val context: Context) : ISyncManager {
    private lateinit var nsdManager: NsdManager
    private var registrationListener: NsdManager.RegistrationListener? = null
    private var discoveryListener: NsdManager.DiscoveryListener? = null


    override suspend fun startDiscovery(onDeviceFound: (ip: String, port: Int, timeStamp: Long) -> Unit) {
        nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager

        discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(serviceType: String) {}
            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                if (serviceInfo.serviceType == syncServiceType &&
                    serviceInfo.serviceName.startsWith(syncServiceName)
                ) {
                    nsdManager.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                        override fun onServiceResolved(resolvedInfo: NsdServiceInfo) {
                            println("Service resolved: ${resolvedInfo.serviceName}")
                            val host = resolvedInfo.host?.hostAddress
                            val port = resolvedInfo.port
                            val timestamp = resolvedInfo.attributes[timeStampAttribute]
                            val timeStamp = timestamp?.let { String(it) }?.toLongOrNull() ?: 0L
                            if (host != null && !resolvedInfo.serviceName.contains(appPlatform.toString())) {
                                onDeviceFound(host, port, timeStamp)
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

        nsdManager.discoverServices(syncServiceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    override suspend fun stopDiscovery() {
        discoveryListener?.let { nsdManager.stopServiceDiscovery(it) }
    }

    override suspend fun advertiseService(port: Int, timeStamp: Long) {
        nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
        val serviceInfo = NsdServiceInfo().apply {
            serviceName = syncServiceName + appPlatform
            serviceType = syncServiceType.removeSuffix("local.") // TODO biggest issue right now
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