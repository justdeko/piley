package com.dk.piley.model.sync

import com.dk.piley.model.sync.model.SyncDevice
import com.dk.piley.util.appPlatform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceInfo
import javax.jmdns.ServiceListener

class SyncManager : ISyncManager {
    private var jmdns: JmDNS? = null
    private var listener: ServiceListener? = null
    val seenServices = mutableSetOf<String>()

    override suspend fun startDiscovery(serviceId: String, onDeviceFound: (syncDevice: SyncDevice) -> Unit) {
        println("Starting discovery...")
        instantiateJmDNS()

        listener = object : ServiceListener {
            override fun serviceAdded(event: ServiceEvent) {
                println("service added: ${event.name}")
                jmdns?.requestServiceInfo(event.type, event.name, true)
            }

            override fun serviceRemoved(event: ServiceEvent) {}

            override fun serviceResolved(event: ServiceEvent) {
                val address = event.info.server
                val port = event.info.port
                val id = "$address:$port"
                if (seenServices.add(id)) {
                    println("New service discovered: $id")
                } else {
                    return
                }
                val timestamp = event.info.getPropertyString(timeStampAttribute)?.toLongOrNull()
                val serviceName = event.info.name
                if (address != null && !serviceName.contains(serviceId)) {
                    val syncDevice = SyncDevice(
                        name = serviceName,
                        hostName = address,
                        port = port,
                        lastSynced = timestamp ?: 0L
                    )
                    onDeviceFound(syncDevice)
                }
            }
        }

        jmdns?.addServiceListener(syncServiceType, listener)
    }

    override suspend fun stopDiscovery() {
        listener?.let { jmdns?.removeServiceListener(syncServiceType, it) }
        withContext(Dispatchers.IO) {
            jmdns?.close()
            jmdns = null
        }
    }

    override suspend fun advertiseService(port: Int, timeStamp: Long, serviceId: String) {
        instantiateJmDNS()
        val txtRecord = mapOf(timeStampAttribute to timeStamp.toString())
        val serviceInfo = ServiceInfo.create(
            /* type = */ syncServiceType,
            /* name = */ syncServiceName + serviceId + "_" + appPlatform,
            /* port = */ port,
            /* weight = */ 0,
            /* priority = */ 0,
            /* props = */ txtRecord
        )
        withContext(Dispatchers.IO) {
            jmdns?.registerService(serviceInfo)
        }
    }

    override suspend fun stopAdvertising() {
        withContext(Dispatchers.IO) {
            jmdns?.unregisterAllServices()
            jmdns?.close()
            jmdns = null
        }
    }

    private fun instantiateJmDNS() {
        if (jmdns == null) {
            jmdns = JmDNS.create(InetAddress.getLocalHost())
        }
    }
}


