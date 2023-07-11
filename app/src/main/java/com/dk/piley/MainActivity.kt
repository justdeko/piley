package com.dk.piley

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.dk.piley.ui.nav.BottomNavigationBar
import com.dk.piley.ui.nav.DEEPLINK_ROOT
import com.dk.piley.ui.nav.Screen
import com.dk.piley.ui.nav.navItems
import com.dk.piley.ui.nav.pileScreen
import com.dk.piley.ui.nav.taskScreen
import com.dk.piley.ui.pile.PileScreen
import com.dk.piley.ui.piles.PileDetailScreen
import com.dk.piley.ui.piles.PileOverviewScreen
import com.dk.piley.ui.profile.ProfileScreen
import com.dk.piley.ui.settings.SettingsScreen
import com.dk.piley.ui.signin.SignInScreen
import com.dk.piley.ui.splash.SplashScreen
import com.dk.piley.ui.task.TaskDetailScreen
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.ui.theme.ThemeHostScreen
import com.dk.piley.util.isDarkMode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // set initial theme
        if (isDarkMode()) {
            setTheme(R.style.Theme_Piley_Dark)
        } else setTheme(R.style.Theme_Piley_Light)

        super.onCreate(savedInstanceState)
        setContent {
            ThemeHostScreen {
                Home()
            }
        }
    }
}

@Composable
fun Home(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navigationBarShown = rememberSaveable { (mutableStateOf(false)) }
    val snackbarHostState = remember { SnackbarHostState() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    // hide navigation bar on detail screen
    navigationBarShown.value =
        navItems.map { it.route }.contains(navBackStackEntry?.destination?.route)

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavigationBar(
                isVisible = navigationBarShown.value, navController = navController
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        NavHost(navController, startDestination = Screen.Splash.route) {
            composable(Screen.Splash.route) {
                SplashScreen(navController = navController)
            }
            composable(Screen.SignIn.route) {
                SignInScreen(navController = navController)
            }
            composable(Screen.Pile.route) {
                PileScreen(
                    Modifier.padding(padding), navController
                )
            }
            composable(Screen.Piles.route) {
                PileOverviewScreen(
                    Modifier.padding(padding), navController
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    Modifier.padding(padding), navController
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(navController = navController)
            }
            composable(
                taskScreen.route,
                deepLinks = listOf(navDeepLink {
                    uriPattern = "$DEEPLINK_ROOT/${taskScreen.route}"
                }),
                arguments = listOf(navArgument(taskScreen.identifier) { type = NavType.LongType })
            ) {
                TaskDetailScreen(navController)
            }
            composable(
                pileScreen.route,
                deepLinks = listOf(navDeepLink {
                    uriPattern = "$DEEPLINK_ROOT/${pileScreen.route}"
                }),
                arguments = listOf(navArgument(pileScreen.identifier) { type = NavType.LongType })
            ) {
                PileDetailScreen(navController)
            }
        }
    }
}

@Preview
@Composable
fun HomePreview() {
    PileyTheme(useDarkTheme = true) {
        Home()
    }
}