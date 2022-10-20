package com.dk.piley.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.ui.theme.PileyTheme


@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val viewState by viewModel.state.collectAsState()
    ProfileScreen(modifier = modifier, viewState = viewState)
}

@Composable
private fun ProfileScreen(modifier: Modifier = Modifier, viewState: ProfileViewState) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        UserInfo(name = viewState.userName)
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