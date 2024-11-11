package com.dk.piley.ui.common

import androidx.compose.runtime.Composable

/**
 * Notification permission handler that requests the permission and shows a dialog if denied
 *
 * @param launch when the permission request should be launched
 * @param setPermissionGranted callback function when the permission result is returned
 */
@Composable
expect fun NotificationPermissionHandler(
    launch: Boolean,
    setPermissionGranted: (Boolean) -> Unit
)