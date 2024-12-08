package com.dk.piley

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.dk.piley.model.navigation.Shortcut
import com.dk.piley.ui.HomeScreen
import com.dk.piley.ui.theme.ThemeHostScreen
import org.jetbrains.compose.resources.painterResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.icon_transparent
import java.awt.Dimension

fun main() = application {
    // di init
    Piley().init()
    // main content
    val windowState = rememberWindowState(size = DpSize(900.dp, 600.dp))
    val repository = Piley.getModule().shortcutEventRepository
    Window(
        onCloseRequest = ::exitApplication,
        title = "piley",
        state = windowState,
        icon = painterResource(Res.drawable.icon_transparent)
    ) {
        window.minimumSize = Dimension(400, 600)
        MenuBar {
            Menu("Navigation") {
                Item(
                    "Pile",
                    onClick = { repository.emitShortcutEvent(Shortcut.Pile) },
                    shortcut = KeyShortcut(Key.P, meta = true)
                )
                Item(
                    "Piles",
                    onClick = { repository.emitShortcutEvent(Shortcut.Piles) },
                    shortcut = KeyShortcut(Key.L, meta = true)
                )
                Item(
                    "Profile",
                    onClick = { repository.emitShortcutEvent(Shortcut.Profile) },
                    shortcut = KeyShortcut(Key.I, meta = true)
                )
                Item(
                    "Settings",
                    onClick = { repository.emitShortcutEvent(Shortcut.Settings) },
                    shortcut = KeyShortcut(Key.Comma, meta = true)
                )
                Item(
                    "Navigate Left",
                    onClick = { repository.emitShortcutEvent(Shortcut.NavigateLeft) },
                    shortcut = KeyShortcut(Key.DirectionLeft, meta = true)
                )
                Item(
                    "Navigate Right",
                    onClick = { repository.emitShortcutEvent(Shortcut.NavigateRight) },
                    shortcut = KeyShortcut(Key.DirectionRight, meta = true)
                )
            }
            Menu("Actions") {
                Item(
                    "Complete top task",
                    onClick = { repository.emitShortcutEvent(Shortcut.Done) },
                    shortcut = KeyShortcut(Key.D, meta = true)
                )
                Item(
                    "Delete top task",
                    onClick = { repository.emitShortcutEvent(Shortcut.Delete) },
                    shortcut = KeyShortcut(Key.Backspace, meta = true)
                )
                Item(
                    "Undo delete/complete of top task",
                    onClick = { repository.emitShortcutEvent(Shortcut.Undo) },
                    shortcut = KeyShortcut(Key.Z, meta = true)
                )
            }
            Menu("Window") {
                Item(
                    "Minimize",
                    onClick = { windowState.isMinimized = !windowState.isMinimized },
                    shortcut = KeyShortcut(Key.M, meta = true)
                )
            }
        }
        ThemeHostScreen {
            HomeScreen()
        }
    }
}