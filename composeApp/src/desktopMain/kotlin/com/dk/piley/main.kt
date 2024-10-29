package com.dk.piley

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.dk.piley.ui.HomeScreen
import com.dk.piley.ui.theme.ThemeHostScreen
import org.jetbrains.compose.resources.painterResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.icon_transparent

fun main() = application {
    // di init
    Piley().init()
    // main content
    Window(
        onCloseRequest = ::exitApplication,
        title = "piley",
        // todo use also distribution icon https://github.com/JetBrains/compose-multiplatform/tree/master/tutorials/Native_distributions_and_local_execution#app-icon
        icon = painterResource(Res.drawable.icon_transparent)
    ) {
        ThemeHostScreen {
            HomeScreen()
        }
    }
}