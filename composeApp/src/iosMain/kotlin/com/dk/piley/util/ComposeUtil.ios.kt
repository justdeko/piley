package com.dk.piley.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getScreenHeight(): Dp = LocalWindowInfo.current.containerSize.height.dp

actual val defaultNavBarPadding: Dp = 110.dp