package com.dk.piley.ui.common

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val LocalDim = compositionLocalOf { Dimensions() }

data class Dimensions(
    val default: Dp = 0.dp,
    val mini: Dp = 1.dp,
    val extraSmall: Dp = 2.dp,
    val small: Dp = 4.dp,
    val medium: Dp = 8.dp,
    val mediumLarge: Dp = 12.dp,
    val large: Dp = 16.dp,
    val veryLarge: Dp = 32.dp,
    val extraLarge: Dp = 48.dp
)
