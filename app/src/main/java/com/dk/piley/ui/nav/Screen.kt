package com.dk.piley.ui.nav

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.filled.Start
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Square
import androidx.compose.material.icons.outlined.Start
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.ui.graphics.vector.ImageVector
import com.dk.piley.R

/**
 * Represents a screen of the app in terms of destination
 *
 * @property route screen navigation route
 * @property titleResource screen title resource id
 * @property icon screen icon
 */
sealed class Screen(
    val route: String,
    @StringRes val titleResource: Int,
    val icon: Pair<ImageVector, ImageVector>,
    val argument: String = ""
) {
    data object Pile : Screen(
        "taskPile", R.string.pile, Pair(Icons.Outlined.Home, Icons.Filled.Home), "pileId"
    )

    data object Piles : Screen(
        "piles", R.string.piles, Pair(Icons.Outlined.ViewAgenda, Icons.Filled.ViewAgenda)
    )

    data object Profile : Screen(
        "profile", R.string.profile, Pair(Icons.Outlined.Person, Icons.Filled.Person)
    )

    data object Settings : Screen(
        "settings", R.string.settings, Pair(Icons.Outlined.Settings, Icons.Filled.Settings)
    )

    data object SignIn : Screen(
        "login", R.string.sign_in, Pair(Icons.Outlined.Login, Icons.Filled.Login)
    )

    data object Splash : Screen(
        "splash", R.string.splash_screen, Pair(Icons.Outlined.Square, Icons.Filled.Square)
    )

    data object Intro : Screen(
        "intro", R.string.introduction_screen, Pair(Icons.Outlined.Start, Icons.Filled.Start)
    )
}

sealed class IdentifierScreen(val route: String, val identifier: String, val root: String) {
    data object Task : IdentifierScreen("task/{taskId}", "taskId", "task")
    data object Pile : IdentifierScreen("pile/{pileId}", "pileId", "pile")
}

val navItems = listOf(
    Screen.Pile,
    Screen.Piles,
    Screen.Profile,
)

val taskScreen = IdentifierScreen.Task
val pileScreen = IdentifierScreen.Pile
const val DEEPLINK_ROOT = "piley://piley.app"
