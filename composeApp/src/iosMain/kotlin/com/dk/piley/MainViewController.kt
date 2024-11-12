package com.dk.piley

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.dk.piley.ui.HomeScreen
import com.dk.piley.ui.theme.ThemeHostScreen

fun MainViewController() = ComposeUIViewController {
    ThemeHostScreen {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            HomeScreen()
        }
    }
}