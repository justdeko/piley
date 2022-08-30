package com.dk.piley.ui.nav

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.dk.piley.R

sealed class Screen(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    object Pile : Screen("pile", R.string.pile, Icons.Filled.Home)
    object Profile : Screen("profile", R.string.profile, Icons.Filled.Person)
}

sealed class IdentifierScreen(val route: String, val identifier: String, val root: String) {
    object Task : IdentifierScreen("task/{taskId}", "taskId", "task")
}

val navItems = listOf(
    Screen.Pile,
    Screen.Profile,
)

val taskScreen = IdentifierScreen.Task