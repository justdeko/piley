package com.dk.piley.ui.sync

import androidx.lifecycle.viewModelScope
import com.dk.piley.common.StatefulViewModel
import com.dk.piley.model.PileDatabase
import com.dk.piley.model.backup.IDatabaseExporter
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.sync.SyncCoordinator
import com.dk.piley.model.sync.model.SyncDevice
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock


class SyncViewModel(
    private val syncCoordinator: SyncCoordinator,
    private val databaseExporter: IDatabaseExporter,
    private val pileRepository: PileRepository,
    private val pileDatabase: PileDatabase,
) : StatefulViewModel<SyncViewState>(SyncViewState()) {
    init {
        viewModelScope.launch {
            state.update {
                it.copy(syncDevices = syncCoordinator.getStoredServices())
            }
        }
        startDiscoveryAndAdvertising()

    }

    fun toggleReceiving() {
        viewModelScope.launch {
            val receiving = !state.value.receiving
            state.update { it.copy(receiving = receiving) }
            if (receiving) {
                syncCoordinator.startServer { data ->
                    try {
                        val file = PlatformFile(FileKit.filesDir, "temp.db")
                        file.write(data)
                        pileRepository.mergeDatabases(
                            pileDatabase = pileDatabase,
                            secondaryDbPath = file.absolutePath()
                        )
                        file.delete()
                    } catch (e: Exception) {
                        state.update { it.copy() }
                    }
                    state.update { it.copy(receiving = false) }
                    syncCoordinator.stopServer()
                }
            } else syncCoordinator.stopServer()
        }
    }

    fun uploadData(index: Int) {
        viewModelScope.launch {
            val syncDevice = state.value.syncDevices[index]
            val dataToSend = databaseExporter.getDatabaseFile().readBytes()
            syncCoordinator.sendData(
                hostName = syncDevice.hostName,
                data = dataToSend
            )
        }
    }

    fun stopSync() {
        viewModelScope.launch {
            syncCoordinator.saveServices(state.value.syncDevices)
            syncCoordinator.stopSync()
        }
    }

    private fun startDiscoveryAndAdvertising() {
        syncCoordinator.startAdvertising(Clock.System.now().toEpochMilliseconds())
        syncCoordinator.startDiscovery { syncDevice ->
            viewModelScope.launch {
                state.update { currentState ->
                    val syncDevices = currentState.syncDevices.toMutableSet()
                    syncDevices.removeAll { it.name == syncDevice.name }
                    syncDevices.add(syncDevice)
                    currentState.copy(syncDevices = syncDevices.toList())
                }
            }
        }
    }

    fun setMessage(messageType: Message?) = state.update {
        it.copy(message = messageType)
    }
}

data class SyncViewState(
    val receiving: Boolean = false,
    val syncDevices: List<SyncDevice> = emptyList(),
    val message: Message? = null,
)

enum class Message { Success, Error }