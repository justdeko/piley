package com.dk.piley.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.notification_permission_dialog_confirm_button
import piley.composeapp.generated.resources.notification_permission_dialog_description
import piley.composeapp.generated.resources.notification_permission_dialog_title

/**
 * Request notification permission dialog
 *
 * @param rationaleOpen whether the permission rationale is open
 * @param onDismiss on dialog dismiss
 * @param onLaunchPermissionRequest on launch the permission request
 */
@Composable
fun RequestNotificationPermissionDialog(
    rationaleOpen: Boolean,
    onDismiss: () -> Unit,
    onLaunchPermissionRequest: () -> Unit
) {
    if (rationaleOpen) {
        // permission denied once, show rationale
        AlertDialogHelper(
            title = stringResource(Res.string.notification_permission_dialog_title),
            description = stringResource(Res.string.notification_permission_dialog_description),
            dismissText = null,
            confirmText = stringResource(Res.string.notification_permission_dialog_confirm_button),
            onDismiss = onDismiss,
            onConfirm = {
                onLaunchPermissionRequest()
                onDismiss()
            }
        )
    } else {
        // permission granted or forever denied
        LaunchedEffect(key1 = Unit, block = { onLaunchPermissionRequest() })
        onDismiss()
    }
}