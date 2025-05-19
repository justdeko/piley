package com.dk.piley.ui.sync

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.outlined.Publish
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.Piley
import com.dk.piley.model.sync.model.SyncDevice
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.common.TitleTopAppBar
import com.dk.piley.util.IndefiniteProgressBar
import com.dk.piley.util.MediumSpacer
import com.dk.piley.util.Platform
import com.dk.piley.util.defaultPadding

@Composable
fun SyncScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: SyncViewModel = viewModel {
        SyncViewModel(
            syncCoordinator = Piley.getModule().syncCoordinator,
            databaseExporter = Piley.getModule().databaseExporter,
            pileRepository = Piley.getModule().pileRepository,
            pileDatabase = Piley.getModule().pileDatabase,
        )
    }
) {
    val viewState by viewModel.state.collectAsState()
    SyncScreen(
        modifier = modifier,
        viewState = viewState,
        onCloseSync = {
            viewModel.stopSync()
            navController.popBackStack()
        },
        onStartUpload = { viewModel.uploadData(it) },
        onStartReceiving = { viewModel.toggleReceiving() }
    )
}

@Composable
internal fun SyncScreen(
    modifier: Modifier = Modifier,
    viewState: SyncViewState,
    onCloseSync: () -> Unit,
    onStartUpload: (Int) -> Unit = {},
    onStartReceiving: () -> Unit = {},
) {
    Column(modifier = modifier.fillMaxSize().defaultPadding()) {
        IndefiniteProgressBar(visible = viewState.loading)
        TitleTopAppBar(
            textValue = "Sync",
            justTitle = true,
            onButtonClick = onCloseSync,
            contentDescription = "close sync screen"
        )
        if (viewState.syncDevices.isNotEmpty()) {
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                viewState.syncDevices.forEachIndexed { index, syncDevice ->
                    SyncItem(
                        syncDevice = syncDevice,
                        onSend = { onStartUpload(index) },
                        enabled = !viewState.loading
                    )
                    if (index != viewState.syncDevices.lastIndex) {
                        HorizontalDivider()
                    }
                }
            }
        } else {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                MediumSpacer()
                Text(
                    text = "Discovering devices...",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
        Button(
            onClick = onStartReceiving,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(text = if (viewState.receiving) "Stop Receiving" else "Receive Data")
        }
    }
}

@Composable
private fun SyncItem(
    syncDevice: SyncDevice,
    onSend: () -> Unit,
    enabled: Boolean = true,
) {
    val dim = LocalDim.current
    val platformIcon = when (syncDevice.platform) {
        Platform.ANDROID -> Icons.Default.Android
        Platform.IOS -> Icons.Default.PhoneIphone // TODO use iOS icon
        Platform.DESKTOP -> Icons.Default.Computer
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(dim.veryLarge),
            imageVector = platformIcon,
            contentDescription = "platform icon",
            tint = MaterialTheme.colorScheme.tertiary
        )
        MediumSpacer()
        Text(
            modifier = Modifier.weight(1f),
            text = syncDevice.name,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.labelLarge
        )
        IconButton(
            enabled = enabled,
            onClick = { onSend() },
            content = {
                Icon(
                    modifier = Modifier.size(dim.veryLarge),
                    imageVector = Icons.Outlined.Publish,
                    contentDescription = "Send",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
    }
}