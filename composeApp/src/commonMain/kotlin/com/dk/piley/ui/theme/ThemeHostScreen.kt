package com.dk.piley.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dk.piley.Piley
import com.dk.piley.model.user.NightMode

@Composable
fun ThemeHostScreen(
    viewModel: ThemeViewModel = viewModel { ThemeViewModel(Piley.getModule().userRepository) },
    setSystemUsingTheme: @Composable (Boolean) -> Unit = {},
    customColorSchemeProvider: @Composable (ThemeViewState, Boolean) -> ColorScheme? = { _, _ -> null },
    content: @Composable () -> Unit
) {
    val viewState by viewModel.state.collectAsState()
    val nightModeEnabled = when (viewState.nightModeEnabled) {
        NightMode.SYSTEM -> isSystemInDarkTheme()
        NightMode.ENABLED -> true
        NightMode.DISABLED -> false
    }
    // wrap content inside compose theme
    PileyTheme(
        useDarkTheme = nightModeEnabled,
        customColors = customColorSchemeProvider(viewState, nightModeEnabled)
    ) {
        setSystemUsingTheme(nightModeEnabled)
        content()
    }
}