package com.dk.piley

import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.ui.window.ComposeUIViewController
import com.dk.piley.ui.HomeScreen
import com.dk.piley.ui.theme.ThemeHostScreen

@OptIn(ExperimentalComposeApi::class)
fun MainViewController() = ComposeUIViewController(configure = {
    // TODO: check if this can be removed: https://youtrack.jetbrains.com/issue/CMP-7943
    opaque = false
}) {
    ThemeHostScreen {
        HomeScreen()
    }
}