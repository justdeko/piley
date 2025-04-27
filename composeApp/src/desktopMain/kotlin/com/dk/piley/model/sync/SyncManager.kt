package com.dk.piley.model.sync

import java.net.InetAddress
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceListener

class SyncManager: ISyncManager {
    private var jmdns: JmDNS? = null
    private var listener: ServiceListener? = null

    override suspend fun startDiscovery(onDeviceFound: (ip: String, port: Int) -> Unit) {
        jmdns = JmDNS.create(InetAddress.getLocalHost())

        listener = object : ServiceListener {
            override fun serviceAdded(event: ServiceEvent) {
                jmdns?.requestServiceInfo(event.type, event.name, true)
            }

            override fun serviceRemoved(event: ServiceEvent) {}

            override fun serviceResolved(event: ServiceEvent) {
                val address = event.info.inetAddresses.firstOrNull()?.hostAddress
                val port = event.info.port
                if (address != null) {
                    onDeviceFound(address, port)
                }
            }
        }

        jmdns?.addServiceListener(serviceType, listener)
    }

    override suspend fun stopDiscovery() {
        listener?.let { jmdns?.removeServiceListener(serviceType, it) }
        jmdns?.close()
    }

    override suspend fun advertiseService(port: Int) {
        jmdns = JmDNS.create(InetAddress.getLocalHost())
        val serviceInfo = javax.jmdns.ServiceInfo.create(serviceType, serviceName, port, "piley sync service")
        jmdns?.registerService(serviceInfo)
    }

    override suspend fun stopAdvertising() {
        jmdns?.unregisterAllServices()
        jmdns?.close()
    }
}


