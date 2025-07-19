package com.dk.piley.ui.common

import androidx.compose.runtime.Composable

@Composable
expect fun CalendarPermissionHandler(
    launch: Boolean,
    setPermissionGranted: (Boolean) -> Unit
)