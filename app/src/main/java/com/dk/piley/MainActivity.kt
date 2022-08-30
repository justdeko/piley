package com.dk.piley

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dk.piley.ui.nav.BottomNavigationBar
import com.dk.piley.ui.nav.Screen
import com.dk.piley.ui.nav.taskScreen
import com.dk.piley.ui.pile.PileScreen
import com.dk.piley.ui.profile.ProfileScreen
import com.dk.piley.ui.task.TaskDetailScreen
import com.dk.piley.ui.theme.PileyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    // hide navigation bar on detail screen
    navigationBarShown.value =
        navBackStackEntry?.destination?.route?.startsWith(taskScreen.root) == false

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavigationBar(
                isVisible = navigationBarShown.value,
                navController = navController
            )
        }
    ) { padding ->
        NavHost(navController, startDestination = Screen.Pile.route) {
            composable(Screen.Pile.route) {
                PileScreen(
                    Modifier.padding(padding),
                    navController
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    Modifier.padding(padding),
                    navController
                )
            }
            composable(
                taskScreen.route,
                arguments = listOf(navArgument(taskScreen.identifier) { type = NavType.LongType })
            ) { navBackStackEntry ->
                TaskDetailScreen(
                    navBackStackEntry.arguments?.getLong(
                        taskScreen.identifier
                    ),
                    navController
                )
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