package com.dk.piley.ui.sync

import androidx.lifecycle.viewModelScope
import com.dk.piley.common.StatefulViewModel
import com.dk.piley.model.PileDatabase
import com.dk.piley.model.backup.IDatabaseExporter
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.sync.SyncCoordinator
import com.dk.piley.model.sync.SyncState
import com.dk.piley.model.sync.model.SyncDevice
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.flow.map
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
            collectState(
                syncCoordinator.syncStateFlow
                    .map { SyncViewState(syncState = it) }
            )
            syncCoordinator.startServer {

            }
        }
    }

    fun startSync() {
        viewModelScope.launch {
            val databaseData = databaseExporter.getDatabaseFile().readBytes()
            syncCoordinator.startSync(
                lastEditedTimeStamp = Clock.System.now().epochSeconds,
                dataToSend = databaseData,
            ) { data ->
                // import db file
                viewModelScope.launch {
                    val file = PlatformFile(FileKit.filesDir, "temp.db")
                    file.write(data)
                    pileRepository.mergeDatabases(
                        pileDatabase = pileDatabase,
                        secondaryDbPath = file.absolutePath()
                    )
                }
            }
        }
    }

    fun stopSync() {
        viewModelScope.launch {
            syncCoordinator.stopSync()
        }
    }

    fun startSync(index: Int) {

    }

    fun stopSync(index: Int) {

    }

    fun startDiscovery() {
        state.update { it.copy(discoveryRunning = true) }
    }
}

data class SyncViewState(
    val syncState: SyncState = SyncState.Idle,
    val discoveryRunning: Boolean = false,
    val loading: Boolean = false,
    val syncDevices: List<SyncDevice> = emptyList(),
)