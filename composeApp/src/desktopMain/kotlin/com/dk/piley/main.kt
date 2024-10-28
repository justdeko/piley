package com.dk.piley

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.dk.piley.ui.HomeScreen
import com.dk.piley.ui.theme.ThemeHostScreen

fun main() = application {
    // di init
    Piley().init()
    // main content
    Window(
        onCloseRequest = ::exitApplication,
        title = "piley",
    ) {
        ThemeHostScreen {
            HomeScreen()
        }
    }
}