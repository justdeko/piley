package com.dk.piley.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dk.piley.Piley
import com.dk.piley.model.user.NightMode
import com.dk.piley.ui.viewModelFactory

/**
 * Theme host screen for displaying themed content
 *
 * @param viewModel theme view model
 * @param content themed to display
 */
@Composable
fun ThemeHostScreen(
    viewModel: ThemeViewModel = viewModel(factory = viewModelFactory { ThemeViewModel(Piley.appModule.userRepository) }),
    setNonComposeTheme: (Boolean) -> Unit = {},
    content: @Composable () -> Unit
) {
    val viewState by viewModel.state.collectAsState()
    val nightModeEnabled = when (viewState.nightModeEnabled) {
        NightMode.SYSTEM -> isSystemInDarkTheme()
        NightMode.ENABLED -> true
        NightMode.DISABLED -> false
    }
    setNonComposeTheme(nightModeEnabled)
    // wrap content inside compose theme
    PileyTheme(nightModeEnabled, viewState.dynamicColorEnabled) {
        // set status bar color and icons color
        val activity = LocalView.current.context as Activity
        val window = activity.window
        window.statusBarColor = MaterialTheme.colorScheme.background.toArgb()
        window.navigationBarColor = MaterialTheme.colorScheme.surfaceContainer.toArgb()
        val wic = WindowCompat.getInsetsController(window, window.decorView)
        wic.isAppearanceLightStatusBars = !nightModeEnabled
        content()
    }
}