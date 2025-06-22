package com.dk.piley.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.dk.piley.util.AlertDialogHelper

/**
 * Request permission dialog
 *
 * @param rationaleOpen whether the permission rationale is open
 * @param onDismiss on dialog dismiss
 * @param onLaunchPermissionRequest on launch the permission request
 */
@Composable
fun RequestPermissionDialog(
    rationaleOpen: Boolean,
    title : String,
    description: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onLaunchPermissionRequest: () -> Unit
) {
    if (rationaleOpen) {
        // permission denied once, show rationale
        AlertDialogHelper(
            title = title,
            description = description,
            dismissText = null,
            confirmText = confirmText,
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