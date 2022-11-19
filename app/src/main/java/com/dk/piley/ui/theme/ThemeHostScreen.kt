package com.dk.piley.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.dk.piley.R
import com.dk.piley.model.user.NightMode
import com.dk.piley.ui.util.isDarkMode
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun ThemeHostScreen(viewModel: ThemeViewModel = hiltViewModel(), content: @Composable () -> Unit) {
    val viewState by viewModel.state.collectAsState()
    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current
    val nightModeEnabled = when (viewState.nightModeEnabled) {
        NightMode.SYSTEM -> context.isDarkMode()
        NightMode.ENABLED -> true
        NightMode.DISABLED -> false
    }
    // set theme for non-compose UI elements
    val mainTheme = if (nightModeEnabled) R.style.Theme_Piley_Dark else R.style.Theme_Piley_Light
    context.setTheme(mainTheme)
    // set status bar color and icons color
    val statusBarColor =
        if (nightModeEnabled) md_theme_dark_background else md_theme_light_background
    systemUiController.setSystemBarsColor(
        color = statusBarColor,
        darkIcons = !nightModeEnabled
    )
    // wrap content inside compose theme
    PileyTheme(nightModeEnabled, viewState.dynamicColorEnabled) {
        content()
    }
}