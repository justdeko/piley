package com.dk.piley.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
actual fun getScreenHeight(): Dp = LocalConfiguration.current
    .screenHeightDp
    .dp

actual val defaultNavBarPadding: Dp = 80.dp