package com.dk.piley

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
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
import com.dk.piley.ui.nav.taskScreen
import com.dk.piley.ui.pile.PileScreen
import com.dk.piley.ui.piles.PileOverviewScreen
import com.dk.piley.ui.profile.ProfileScreen
import com.dk.piley.ui.task.TaskDetailScreen
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.ui.util.isDarkMode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // set initial theme
        if (this.isDarkMode()) {
            setTheme(R.style.Theme_Piley_Dark)
        } else {
            setTheme(R.style.Theme_Piley_Light)
        }
        super.onCreate(savedInstanceState)
        setContent {
            PileyTheme {
                Home()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navigationBarShown = rememberSaveable { (mutableStateOf(true)) }
    val snackbarHostState = remember { SnackbarHostState() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    // hide navigation bar on detail screen
    navigationBarShown.value =
        navBackStackEntry?.destination?.route?.startsWith(taskScreen.root) == false

    Scaffold(modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavigationBar(
                isVisible = navigationBarShown.value, navController = navController
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        NavHost(navController, startDestination = Screen.Pile.route) {
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
            composable(
                taskScreen.route,
                deepLinks = listOf(navDeepLink {
                    uriPattern = "$DEEPLINK_ROOT/${taskScreen.route}"
                }),
                arguments = listOf(navArgument(taskScreen.identifier) { type = NavType.LongType })
            ) {
                TaskDetailScreen(navController)
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