package com.dk.piley.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.dk.piley.Piley
import com.dk.piley.ui.intro.IntroScreen
import com.dk.piley.ui.nav.DEEPLINK_ROOT
import com.dk.piley.ui.nav.NavigationBar
import com.dk.piley.ui.nav.Screen
import com.dk.piley.ui.nav.navItems
import com.dk.piley.ui.nav.pileScreen
import com.dk.piley.ui.nav.taskScreen
import com.dk.piley.ui.pile.PileScreen
import com.dk.piley.ui.piles.PileDetailScreen
import com.dk.piley.ui.piles.PileOverviewScreen
import com.dk.piley.ui.profile.ProfileScreen
import com.dk.piley.ui.settings.SettingsScreen
import com.dk.piley.ui.splash.SplashScreen
import com.dk.piley.ui.task.TaskDetailScreen


/**
 * Home representing main app view
 *
 * @param modifier generic modifier
 * @param initialMessage initial user message
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    initialMessage: String? = null,
    viewModel: HomeViewModel = viewModel {
        HomeViewModel(
            taskRepository = Piley.getModule().taskRepository,
            notificationRepository = Piley.getModule().notificationRepository,
            navigationEventRepository = Piley.getModule().navigationEventRepository,
            userRepository = Piley.getModule().userRepository
        )
    },
    onFinishActivity: () -> Unit = {},
) {
    val homeState by viewModel.state.collectAsState()
    val navController = rememberNavController()
    var navigationBarShown by rememberSaveable { (mutableStateOf(false)) }
    val snackbarHostState = remember { SnackbarHostState() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // hide navigation bar on screens that are not in the main view
    navigationBarShown = navItems.map { it.route }
        .any { navBackStackEntry?.destination?.route?.contains(it) ?: false }

    // display initial message if not null
    if (initialMessage != null) {
        LaunchedEffect(true) { snackbarHostState.showSnackbar(initialMessage) }
    }

    homeState.message?.let {
        LaunchedEffect(it, snackbarHostState) { snackbarHostState.showSnackbar(it) }
    }
    homeState.navigationEvent?.let {
        LaunchedEffect(it, navController) {
            println("incoming nav event: $it")
            // prevent navigating twice if the app is currently on the task screen
            if (navBackStackEntry?.destination?.route?.startsWith(taskScreen.route) == false
                || it.contains(taskScreen.optionalArguments.first().name)
            ) {
                navController.navigate(it)
                viewModel.onConsumeNavEvent()
            }
        }
    }

    Scaffold(
        modifier = modifier.then(
            // safe padding on all screens except splash screen
            if (navBackStackEntry?.destination?.route == Screen.Splash.route) Modifier
            // TODO adapt for sdk 35
            else Modifier.windowInsetsPadding(WindowInsets.statusBars) // TODO fix this for ios with box and surfaceContainer color on the lower bottom half
        ),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        val startDestination =
            if (homeState.skipSplashScreen) Screen.Pile.route else Screen.Splash.route
        NavigationBar(
            modifier = Modifier.fillMaxSize().consumeWindowInsets(padding),
            isVisible = navigationBarShown,
            navController = navController
        ) {
            NavHost(navController, startDestination = startDestination) {
                composable(Screen.Splash.route) {
                    SplashScreen(navController = navController)
                }
                composable(route = Screen.Intro.route, arguments = Screen.Intro.optionalArguments) {
                    IntroScreen(navController = navController)
                }
                composable(
                    "${Screen.Pile.route}?${Screen.Pile.argument}={${Screen.Pile.argument}}",
                    arguments = listOf(navArgument(Screen.Pile.argument) {
                        type = NavType.LongType
                        defaultValue = -1L // default value representing no pile passed
                    })
                ) {
                    PileScreen(
                        navController = navController, // TODO: don't pass navController
                        snackbarHostState = snackbarHostState
                    )
                }
                composable(Screen.Piles.route) {
                    PileOverviewScreen(
                        navController = navController
                    )
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        navController = navController,
                        snackbarHostState = snackbarHostState
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        navController = navController,
                        snackbarHostState = snackbarHostState
                    )
                }
                composable(
                    taskScreen.route,
                    deepLinks = listOf(navDeepLink {
                        uriPattern = "$DEEPLINK_ROOT/${taskScreen.route}"
                    }),
                    arguments = listOf(navArgument(taskScreen.identifier) {
                        type = NavType.LongType
                    }) + taskScreen.optionalArguments
                ) {
                    TaskDetailScreen(
                        navController = navController,
                        onFinish = onFinishActivity
                    )
                }
                composable(
                    pileScreen.route,
                    deepLinks = listOf(navDeepLink {
                        uriPattern = "$DEEPLINK_ROOT/${pileScreen.route}"
                    }),
                    arguments = listOf(navArgument(pileScreen.identifier) {
                        type = NavType.LongType
                    })
                ) {
                    PileDetailScreen(
                        navController = navController
                    )
                }
            }
        }
    }
}