package com.dk.piley.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dk.piley.Piley
import com.dk.piley.model.user.NightMode
import com.dk.piley.ui.viewModelFactory

@Composable
fun ThemeHostScreen(
    viewModel: ThemeViewModel = viewModel(factory = viewModelFactory { ThemeViewModel(Piley.appModule.userRepository) }),
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