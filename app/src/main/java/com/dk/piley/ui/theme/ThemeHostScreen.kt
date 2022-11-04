package com.dk.piley.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.dk.piley.model.user.NightMode
import com.dk.piley.ui.util.isDarkMode

@Composable
fun ThemeHostScreen(viewModel: ThemeViewModel = hiltViewModel(), content: @Composable () -> Unit) {
    val viewState by viewModel.state.collectAsState()
    val context = LocalContext.current
    val nightModeEnabled = when (viewState.nightModeEnabled) {
        NightMode.SYSTEM -> context.isDarkMode()
        NightMode.ENABLED -> true
        NightMode.DISABLED -> false
    }
    PileyTheme(nightModeEnabled, viewState.dynamicColorEnabled) {
        content()
    }
}