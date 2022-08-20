package com.dk.piley

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dk.piley.model.task.Task
import com.dk.piley.ui.nav.Screen
import com.dk.piley.ui.nav.navItems
import com.dk.piley.ui.pile.AddTaskField
import com.dk.piley.ui.pile.Pile
import com.dk.piley.ui.pile.PileTask
import com.dk.piley.ui.pile.PileViewModel
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
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                navItems.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                screen.icon,
                                tint = MaterialTheme.colorScheme.secondary,
                                contentDescription = null,
                            )
                        },
                        label = {
                            Text(
                                stringResource(screen.resourceId),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(navController, startDestination = Screen.Pile.route, Modifier.padding(padding)) {
            composable(Screen.Pile.route) { PileScreen(navController) }
            composable(Screen.Profile.route) { ProfileScreen(navController) }
        }
    }
}

@Composable
fun ProfileScreen(navController: NavHostController = rememberNavController()) {
    Text("Hi there, TODO")
}

@Composable
fun PileScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: PileViewModel = hiltViewModel()
) {
    val viewState by viewModel.state.collectAsState()
    val context = LocalContext.current
    var query by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue("")
        )
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Pile(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            viewState.tasks
        ) { viewModel.delete(it) }
        AddTaskField(
            value = query,
            onChange = { v: TextFieldValue -> query = v },
            onDone = {
                if (query.text.isNotBlank()) {
                    viewModel.add(query.text)
                } else {
                    Toast.makeText(context, "Task can't be empty", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

@Preview
@Composable
fun HomePreview() {
    PileyTheme(useDarkTheme = true) {
        Home()
    }
}