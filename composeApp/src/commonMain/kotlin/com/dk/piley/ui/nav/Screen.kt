package com.dk.piley.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.filled.Start
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Square
import androidx.compose.material.icons.outlined.Start
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.jetbrains.compose.resources.StringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.introduction_screen
import piley.composeapp.generated.resources.pile
import piley.composeapp.generated.resources.piles
import piley.composeapp.generated.resources.profile
import piley.composeapp.generated.resources.settings
import piley.composeapp.generated.resources.splash_screen

/**
 * Represents a screen of the app in terms of destination
 *
 * @property route screen navigation route
 * @property titleResource screen title resource id
 * @property icon screen icon
 * @property optionalArguments list of optional nav arguments
 */
sealed class Screen(
    val route: String,
    val titleResource: StringResource,
    val icon: Pair<ImageVector, ImageVector>,
    val argument: String = "",
    val optionalArguments: List<NamedNavArgument> = emptyList(),
) {
    data object Pile : Screen(
        "taskPile", Res.string.pile, Pair(Icons.Outlined.Home, Icons.Filled.Home), "pileId"
    )

    data object Piles : Screen(
        "piles", Res.string.piles, Pair(Icons.Outlined.ViewAgenda, Icons.Filled.ViewAgenda)
    )

    data object Profile : Screen(
        "profile", Res.string.profile, Pair(Icons.Outlined.Person, Icons.Filled.Person)
    )

    data object Settings : Screen(
        "settings", Res.string.settings, Pair(Icons.Outlined.Settings, Icons.Filled.Settings)
    )

    data object Splash : Screen(
        "splash", Res.string.splash_screen, Pair(Icons.Outlined.Square, Icons.Filled.Square)
    )

    data object Intro : Screen(
        "intro", Res.string.introduction_screen, Pair(Icons.Outlined.Start, Icons.Filled.Start),
    )
}

sealed class IdentifierScreen(
    val route: String,
    val identifier: String,
    val root: String,
    val optionalArguments: List<NamedNavArgument> = emptyList()
) {
    data object Task : IdentifierScreen(
        route = "task/{taskId}?delay={delay}",
        identifier = "taskId",
        root = "task",
        optionalArguments = listOf(
            navArgument("delay") {
                defaultValue = false
                type = NavType.BoolType
            }
        )
    )

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
