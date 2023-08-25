package com.dk.piley.util

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import com.dk.piley.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

/**
 * Request notification permission dialog
 *
 * @param rationaleOpen whether the permission rationale is open
 * @param onDismiss on dialog dismiss
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestNotificationPermissionDialog(rationaleOpen: Boolean, onDismiss: () -> Unit) {
    val permissionState =
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    if (!permissionState.status.isGranted && permissionState.status.shouldShowRationale && rationaleOpen) {
        // permission denied once, show rationale
        AlertDialogHelper(
            title = stringResource(R.string.notification_permission_dialog_title),
            description = stringResource(R.string.notification_permission_dialog_description),
            dismissText = null,
            confirmText = stringResource(R.string.notification_permission_dialog_confirm_button),
            onDismiss = onDismiss,
            onConfirm = {
                permissionState.launchPermissionRequest()
                onDismiss()
            }
        )
    } else {
        // permission granted or forever denied
        LaunchedEffect(key1 = Unit, block = { permissionState.launchPermissionRequest() })
        onDismiss()
    }
}