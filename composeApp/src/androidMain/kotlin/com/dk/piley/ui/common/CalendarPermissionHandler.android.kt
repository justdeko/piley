package com.dk.piley.ui.common

import androidx.compose.runtime.Composable

@Composable
actual fun CalendarPermissionHandler(
    launch: Boolean,
    setPermissionGranted: (Boolean) -> Unit
) {
    setPermissionGranted(false) // TODO request permission
}