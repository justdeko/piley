package com.dk.piley.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
actual fun getScreenHeight(): Dp = LocalWindowInfo.current.containerDpSize.height

actual val defaultNavBarPadding: Dp = 80.dp