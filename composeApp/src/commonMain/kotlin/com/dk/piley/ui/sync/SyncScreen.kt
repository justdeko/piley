package com.dk.piley.ui.sync

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.outlined.Close
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
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
import com.dk.piley.util.TinySpacer
import com.dk.piley.util.defaultPadding
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.discovering_devices
import piley.composeapp.generated.resources.message_send_error
import piley.composeapp.generated.resources.message_send_success
import piley.composeapp.generated.resources.message_sync_error
import piley.composeapp.generated.resources.message_sync_success
import piley.composeapp.generated.resources.receive_data
import piley.composeapp.generated.resources.stop_receiving
import piley.composeapp.generated.resources.sync_screen

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

    viewState.message?.let { message ->
        val messageString = when (message) {
            Message.ErrorReceiving -> stringResource(Res.string.message_sync_error)
            Message.ErrorSending -> stringResource(Res.string.message_send_error)
            Message.SuccessReceiving -> stringResource(Res.string.message_sync_success)
            Message.SuccessSending -> stringResource(Res.string.message_send_success)
        }
        LaunchedEffect(message, snackbarHostState) {
            snackbarHostState.showSnackbar(messageString)
            // reset message
            viewModel.setMessage(null)
        }
    }

    SyncScreen(
        modifier = modifier,
        viewState = viewState,
        onCloseSync = {
            viewModel.stopSync()
            navController.popBackStack()
        },
        onStartUpload = { viewModel.uploadData(it) },
        onRemove = { viewModel.removeDevice(it) },
        onStartReceiving = { viewModel.toggleReceiving() }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun SyncScreen(
    modifier: Modifier = Modifier,
    viewState: SyncViewState,
    onCloseSync: () -> Unit,
    onStartUpload: (Int) -> Unit = {},
    onRemove: (Int) -> Unit = {},
    onStartReceiving: () -> Unit = {},
) {
    BackHandler { onCloseSync() }

    Column(modifier = modifier.fillMaxSize().defaultPadding()) {
        IndefiniteProgressBar(visible = viewState.receiving)
        TitleTopAppBar(
            textValue = stringResource(Res.string.sync_screen),
            justTitle = true,
            onButtonClick = onCloseSync,
            contentDescription = "close sync screen"
        )
        if (viewState.syncDevices.isNotEmpty()) {
            LazyColumn(Modifier.weight(1f)) {
                viewState.syncDevices.forEachIndexed { index, syncDevice ->
                    item {
                        SyncItem(
                            modifier = Modifier.animateItem(),
                            syncDevice = syncDevice,
                            onSend = { onStartUpload(index) },
                            onRemove = { onRemove(index) }
                        )
                        if (index != viewState.syncDevices.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        } else {
            Column(
                Modifier.fillMaxWidth().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                MediumSpacer()
                Text(
                    text = stringResource(Res.string.discovering_devices),
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
            Text(
                text = stringResource(
                    if (viewState.receiving) Res.string.stop_receiving else Res.string.receive_data
                )
            )
        }
    }
}

@Composable
private fun SyncItem(
    modifier: Modifier = Modifier,
    syncDevice: SyncDevice,
    onSend: () -> Unit,
    onRemove: () -> Unit,
    enabled: Boolean = true,
) {
    val dim = LocalDim.current
    val platformIcon = when (syncDevice.platform) {
        Platform.ANDROID -> Icons.Default.Android
        Platform.IOS -> Icons.Default.PhoneIphone
        Platform.DESKTOP -> Icons.Default.Computer
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(dim.veryLarge),
            imageVector = platformIcon,
            contentDescription = "platform icon",
            tint = MaterialTheme.colorScheme.tertiary
        )
        MediumSpacer()
        Column(Modifier.weight(1f)) {
            Text(
                text = syncDevice.name,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
            TinySpacer()
            Text(
                text = syncDevice.hostName,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelSmall
            )
        }
        IconButton(
            enabled = enabled,
            onClick = onSend,
            content = {
                Icon(
                    modifier = Modifier.size(dim.veryLarge),
                    imageVector = Icons.Outlined.Publish,
                    contentDescription = "Send",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
        IconButton(
            enabled = enabled,
            onClick = onRemove,
            content = {
                Icon(
                    modifier = Modifier.size(dim.veryLarge),
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "remove device",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        )
    }
}