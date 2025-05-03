package com.dk.piley.ui.sync

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.Piley
import com.dk.piley.model.sync.SyncState
import com.dk.piley.ui.common.TitleTopAppBar
import com.dk.piley.ui.common.TwoPaneScreen

@Composable
fun SyncScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: SyncViewModel = viewModel {
        SyncViewModel(
            syncCoordinator = Piley.getModule().syncCoordinator,
            databaseExporter = Piley.getModule().databaseExporter,
        )
    }
) {
    val viewState by viewModel.state.collectAsState()
    SyncScreen(
        modifier = modifier,
        viewState = viewState,
        onCloseSync = { navController.popBackStack() },
        onStartSync = { viewModel.startSync() },
        onStopSync = { viewModel.stopSync() }
    )
}

@Composable
internal fun SyncScreen(
    modifier: Modifier = Modifier,
    viewState: SyncViewState,
    onCloseSync: () -> Unit,
    onStartSync: () -> Unit = {},
    onStopSync: () -> Unit = {},
) {
    Column(modifier = modifier.fillMaxSize()) {
        TitleTopAppBar(
            textValue = "Sync",
            justTitle = true,
            onButtonClick = onCloseSync,
            contentDescription = "close sync screen"
        )
        TwoPaneScreen(
            mainContent = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "State: ${viewState.syncState}")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { onStartSync() },
                            enabled = viewState.syncState == SyncState.Idle
                                    || viewState.syncState == SyncState.Synced
                                    || viewState.syncState == SyncState.Error
                        ) {
                            Text(text = "Start Sync")
                        }
                        Button(
                            onClick = { onStopSync() },
                            enabled =
                                viewState.syncState == SyncState.Syncing
                                        || viewState.syncState == SyncState.Advertising
                                        || viewState.syncState == SyncState.Discovering
                        ) {
                            Text(text = "Stop Sync")
                        }
                    }
                }
            },
            detailContent = {

            }
        )
    }
}