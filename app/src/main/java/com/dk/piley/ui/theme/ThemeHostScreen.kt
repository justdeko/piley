package com.dk.piley.ui.theme

import androidx.compose.material3.MaterialTheme
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
    // wrap content inside compose theme
    PileyTheme(nightModeEnabled, viewState.dynamicColorEnabled) {
        // set status bar color and icons color
        systemUiController.setSystemBarsColor(
            color = MaterialTheme.colorScheme.background,
            darkIcons = !nightModeEnabled
        )
        content()
    }
}