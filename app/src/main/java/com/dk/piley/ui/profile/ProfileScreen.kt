package com.dk.piley.ui.profile

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.ui.theme.PileyTheme


@Composable
fun ProfileScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val viewState by viewModel.state.collectAsState()
    ProfileScreen(viewState = viewState)
}

@Composable
private fun ProfileScreen(modifier: Modifier = Modifier, viewState: ProfileViewState) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        UserInfo(name = "Thomas")
        TaskStats(
            doneCount = viewState.doneTasks,
            deletedCount = viewState.deletedTasks,
            currentCount = viewState.currentTasks
        )
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ProfileScreenPreview() {
    PileyTheme {
        Surface {
            val state = ProfileViewState(0, 2, 3)
            ProfileScreen(viewState = state)
        }
    }
}