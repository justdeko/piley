package com.dk.piley.ui.nav

import androidx.annotation.StringRes
import com.dk.piley.R

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Pile : Screen("pile", R.string.pile)
    object Profile : Screen("profile", R.string.profile)
}

val navItems = listOf(
    Screen.Pile,
    Screen.Profile,
)