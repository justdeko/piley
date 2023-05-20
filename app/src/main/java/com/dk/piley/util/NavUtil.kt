package com.dk.piley.util

import androidx.navigation.NavController

fun NavController.navigateClearBackstack(route: String) = navigate(route) {
    popUpTo(this@navigateClearBackstack.graph.id) {
        inclusive = true
    }
}