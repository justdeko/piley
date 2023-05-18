package com.dk.piley.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.ui.nav.Screen
import com.dk.piley.ui.theme.PileyTheme


@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val viewState by viewModel.state.collectAsState()
    if (viewState.signedOut) {
        navController.navigate(Screen.SignIn.route) { popUpTo(0) }
    }
    ProfileScreen(modifier = modifier,
        viewState = viewState,
        onClickSettings = { navController.navigate(Screen.Settings.route) },
        onSignOut = {
            viewModel.signOut()
        }
    )
}

@Composable
private fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewState: ProfileViewState,
    onClickSettings: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onClickSettings) {
                Icon(
                    Icons.Filled.Settings,
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = "go to settings"
                )
            }
            IconButton(onClick = onSignOut) {
                Icon(
                    Icons.Filled.Logout,
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = "sign out"
                )
            }
        }
        UserInfo(name = viewState.userName)
        Text(
            text = "Statistics",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 16.dp),
            textAlign = TextAlign.Start
        )
        TaskStats(
            doneCount = viewState.doneTasks,
            deletedCount = viewState.deletedTasks,
            currentCount = viewState.currentTasks
        )
    }
}

@PreviewMainScreen
@Composable
fun ProfileScreenPreview() {
    PileyTheme {
        Surface {
            val state = ProfileViewState("Thomas", 0, 2, 3)
            ProfileScreen(viewState = state)
        }
    }
}