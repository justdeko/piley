package com.dk.piley.ui.sync

import androidx.lifecycle.viewModelScope
import com.dk.piley.common.StatefulViewModel
import com.dk.piley.model.backup.IDatabaseExporter
import com.dk.piley.model.backup.ImportResult
import com.dk.piley.model.sync.SyncCoordinator
import com.dk.piley.model.sync.SyncState
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class SyncViewModel(
    private val syncCoordinator: SyncCoordinator,
    private val databaseExporter: IDatabaseExporter
) : StatefulViewModel<SyncViewState>(SyncViewState()) {
    init {
        viewModelScope.launch {
            collectState(
                syncCoordinator.syncStateFlow
                    .map { SyncViewState(syncState = it) }
            )
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
                    databaseExporter.importPileDatabase(file).collect { result ->
                        when (result) {
                            is ImportResult.Success -> {
                                // Handle success
                                println("Import successful")
                            }

                            is ImportResult.Error -> {
                                // Handle error
                                println("Import error: ${result.message}")
                            }
                        }
                    }
                }
            }
        }
    }

    fun stopSync() {
        viewModelScope.launch {
            syncCoordinator.stopSync()
        }
    }
}

data class SyncViewState(
    val syncState: SyncState = SyncState.Idle,
)