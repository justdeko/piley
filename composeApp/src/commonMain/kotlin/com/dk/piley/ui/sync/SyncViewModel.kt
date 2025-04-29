package com.dk.piley.ui.sync

import androidx.lifecycle.viewModelScope
import com.dk.piley.common.StatefulViewModel
import com.dk.piley.model.sync.SyncCoordinator
import com.dk.piley.model.sync.SyncState
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class SyncViewModel(
    private val syncCoordinator: SyncCoordinator
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
        syncCoordinator.startSync(
            lastEditedTimeStamp = Clock.System.now().epochSeconds
        )
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