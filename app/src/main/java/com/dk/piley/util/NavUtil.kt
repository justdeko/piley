package com.dk.piley.util

import androidx.navigation.NavController

/**
 * Navigate to route and clear navigation backstack
 *
 * @param route route to navigate to
 */
fun NavController.navigateClearBackstack(route: String) = navigate(route) {
    popUpTo(this@navigateClearBackstack.graph.id) {
        inclusive = true
    }
}

/**
 * Replace an argument within a navigation route with a specific value
 * e.g. "someRoute?someArgument={myArgument}".withArgument("myArgument", "abc") =>
 * "someRoute?someArgument=abc"
 *
 * @param argument the argument to replace
 * @param value the value to insert
 * @return a modified navigation route containing the argument with the value
 */
fun String.withArgument(argument: String, value: String): String =
    replace("{$argument}", value)