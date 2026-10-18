package com.dk.piley.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.dk.piley.ui.theme.DarkColors
import com.dk.piley.ui.theme.LightColors
import com.dk.piley.ui.theme.ThemeViewState

fun Context.getActivityOrNull(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@Composable
fun getDynamicColorScheme(themeViewState: ThemeViewState, nightModeEnabled: Boolean): ColorScheme {
    val dynamicColor = themeViewState.dynamicColorEnabled
    val colors = if (!nightModeEnabled) {
        if (dynamicColor) {
            dynamicLightColorScheme(LocalContext.current)
        } else LightColors
    } else {
        if (dynamicColor) {
            dynamicDarkColorScheme(LocalContext.current)
        } else DarkColors
    }
    return colors
}