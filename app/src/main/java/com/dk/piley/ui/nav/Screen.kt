package com.dk.piley.ui.nav

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.ui.graphics.vector.ImageVector
import com.dk.piley.R

sealed class Screen(
    val route: String, @StringRes val resourceId: Int, val icon: Pair<ImageVector, ImageVector>
) {
    object Pile : Screen(
        "pile", R.string.pile, Pair(Icons.Outlined.Home, Icons.Filled.Home)
    )

    object Piles : Screen(
        "piles", R.string.piles, Pair(Icons.Outlined.ViewAgenda, Icons.Filled.ViewAgenda)
    )

    object Profile : Screen(
        "profile", R.string.profile, Pair(Icons.Outlined.Person, Icons.Filled.Person)
    )

    object Settings : Screen(
        "settings", R.string.settings, Pair(Icons.Outlined.Settings, Icons.Filled.Settings)
    )
}

sealed class IdentifierScreen(val route: String, val identifier: String, val root: String) {
    object Task : IdentifierScreen("task/{taskId}", "taskId", "task")
}

val navItems = listOf(
    Screen.Pile,
    Screen.Piles,
    Screen.Profile,
)

val taskScreen = IdentifierScreen.Task
const val DEEPLINK_ROOT = "https://piley.app"
