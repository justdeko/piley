package com.dk.piley.ui.nav

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dk.piley.ui.theme.PileyTheme
import org.jetbrains.compose.resources.stringResource

/**
 * App bottom navigation bar
 *
 * @param modifier generic modifier
 * @param isVisible whether the navigation bar is visible
 * @param navController generic nav controller
 */
@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    navController: NavHostController = rememberNavController()
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        content = {
            NavigationBar(modifier = modifier) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                navItems.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any {
                        it.route?.contains(screen.route) ?: false
                    } == true
                    NavigationBarItem(
                        icon = {
                            Icon(
                                if (selected) screen.icon.second else screen.icon.first,
                                tint = MaterialTheme.colorScheme.secondary,
                                contentDescription = null,
                            )
                        },
                        label = {
                            Text(
                                stringResource(screen.titleResource),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to start destination to prevent navigation stack up
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // avoid navigation when same destination
                                launchSingleTop = true
                                // restore state when navigating to previous item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    )
}

@Preview
@Composable
fun BottomNavigationBarPreview() {
    PileyTheme(useDarkTheme = true) {
        BottomNavigationBar()
    }
}