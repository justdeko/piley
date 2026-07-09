package com.dk.piley.model.sync

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import com.dk.piley.model.sync.model.SyncDevice
import com.dk.piley.util.appPlatform

class SyncManager(private val context: Context) : ISyncManager {
    private lateinit var nsdManager: NsdManager
    private var registrationListener: NsdManager.RegistrationListener? = null
    private var discoveryListener: NsdManager.DiscoveryListener? = null


    override suspend fun startDiscovery(serviceId: String, onDeviceFound: (SyncDevice) -> Unit) {
        nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager

        discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(serviceType: String) {}
            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                if (serviceInfo.serviceName.startsWith(syncServiceName)
                    && !serviceInfo.serviceName.contains(appPlatform.toString())
                ) {
                    resolveService(serviceInfo, serviceId, onDeviceFound)
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

    override suspend fun advertiseService(port: Int, timeStamp: Long, serviceId: String) {
        nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
        val serviceInfo = NsdServiceInfo().apply {
            serviceName = syncServiceName + serviceId + "_" + appPlatform
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

    /**
     * Resolve a discovered service to obtain its host and port, then report it once.
     * Uses [NsdManager.registerServiceInfoCallback] on API 34+ and falls back to the
     * deprecated [NsdManager.resolveService] on older devices.
     */
    private fun resolveService(
        serviceInfo: NsdServiceInfo,
        serviceId: String,
        onDeviceFound: (SyncDevice) -> Unit,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            nsdManager.registerServiceInfoCallback(
                serviceInfo,
                context.mainExecutor,
                object : NsdManager.ServiceInfoCallback {
                    override fun onServiceInfoCallbackRegistrationFailed(errorCode: Int) {}
                    override fun onServiceUpdated(updatedInfo: NsdServiceInfo) {
                        val hostName = updatedInfo.hostAddresses.firstOrNull()?.hostName ?: return
                        reportDevice(updatedInfo, hostName, serviceId, onDeviceFound)
                        // resolve once, then stop listening for further updates
                        nsdManager.unregisterServiceInfoCallback(this)
                    }

                    override fun onServiceLost() {}
                    override fun onServiceInfoCallbackUnregistered() {}
                }
            )
        } else {
            @Suppress("DEPRECATION")
            nsdManager.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                override fun onServiceResolved(resolvedInfo: NsdServiceInfo) {
                    @Suppress("DEPRECATION")
                    val hostName = resolvedInfo.host?.hostName ?: return
                    reportDevice(resolvedInfo, hostName, serviceId, onDeviceFound)
                }

                override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}
            })
        }
    }

    private fun reportDevice(
        info: NsdServiceInfo,
        hostName: String,
        serviceId: String,
        onDeviceFound: (SyncDevice) -> Unit,
    ) {
        val timeStamp = info.attributes[timeStampAttribute]?.let { String(it) }?.toLongOrNull() ?: 0L
        if (!info.serviceName.contains(serviceId)) {
            onDeviceFound(
                SyncDevice(
                    name = info.serviceName,
                    hostName = hostName,
                    port = info.port,
                    lastSynced = timeStamp
                )
            )
        }
    }
}