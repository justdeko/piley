package com.dk.piley.model.sync

import com.dk.piley.util.appPlatform
import java.net.InetAddress
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceListener

class SyncManager : ISyncManager {
    private var jmdns: JmDNS? = null
    private var listener: ServiceListener? = null
    val seenServices = mutableSetOf<String>()

    override suspend fun startDiscovery(onDeviceFound: (ip: String, port: Int, timeStamp: Long) -> Unit) {
        println("Starting discovery...")
        jmdns = JmDNS.create(InetAddress.getLocalHost())

        listener = object : ServiceListener {
            override fun serviceAdded(event: ServiceEvent) {
                println("service added: ${event.name}")
                jmdns?.requestServiceInfo(event.type, event.name, true)
            }

            override fun serviceRemoved(event: ServiceEvent) {}

            override fun serviceResolved(event: ServiceEvent) {
                val id = "${event.info.inetAddresses.firstOrNull()?.hostAddress}:${event.info.port}"
                if (seenServices.add(id)) {
                    println("New service discovered: $id")
                } else {
                    return
                }
                val address = event.info.inetAddresses.firstOrNull()?.hostAddress
                val port = event.info.port
                val timestamp = event.info.getPropertyString(timeStampAttribute)?.toLongOrNull()
                val serviceName = event.info.name
                if (address != null && !serviceName.contains(appPlatform.toString())) {
                    onDeviceFound(address, port, timestamp ?: 0L)
                }
            }
        }

        jmdns?.addServiceListener(syncServiceType, listener)
    }

    override suspend fun stopDiscovery() {
        listener?.let { jmdns?.removeServiceListener(syncServiceType, it) }
        jmdns?.close()
    }

    override suspend fun advertiseService(port: Int, timestamp: Long) {
        jmdns = JmDNS.create(InetAddress.getLocalHost())
        val txtRecord = mapOf(timeStampAttribute to timestamp.toString())
        val serviceInfo = javax.jmdns.ServiceInfo.create(
            /* type = */ syncServiceType,
            /* name = */ syncServiceName + appPlatform,
            /* port = */ port,
            /* weight = */ 0,
            /* priority = */ 0,
            /* props = */ txtRecord
        )
        jmdns?.registerService(serviceInfo)
    }

    override suspend fun stopAdvertising() {
        jmdns?.unregisterAllServices()
        jmdns?.close()
    }
}


