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