package com.dk.piley

import androidx.compose.ui.window.ComposeUIViewController
import com.dk.piley.ui.HomeScreen
import com.dk.piley.ui.theme.ThemeHostScreen

fun MainViewController() = ComposeUIViewController {
    ThemeHostScreen {
        HomeScreen()
    }
}