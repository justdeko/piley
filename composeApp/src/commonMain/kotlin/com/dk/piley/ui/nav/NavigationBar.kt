package com.dk.piley.ui.nav

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dk.piley.util.isCurrentDestination
import com.dk.piley.util.isTabletWide
import org.jetbrains.compose.resources.stringResource

/**
 * Navigation bar that adapts to the current window size
 *
 * @param modifier generic modifier
 * @param isVisible whether the navigation bar is visible
 * @param navController the navigation controller
 * @param content the content to display inside the navigation bar container
 */
@Composable
fun NavigationBar(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    navController: NavHostController = rememberNavController(),
    content: @Composable () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isTabletWide = isTabletWide()
    NavigationSuiteScaffold(
        layoutType = if (!isVisible && !isTabletWide) {
            NavigationSuiteType.None
        } else {
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
        },
        navigationSuiteItems = {
            navItems.forEach {
                item(
                    icon = { Icon(it.icon.first, contentDescription = null) },
                    label = { Text(stringResource(it.titleResource)) },
                    selected = it.isCurrentDestination(currentDestination),
                    onClick = { navController.navigate(it.route) }
                )
            }
        },
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        content()
    }
}