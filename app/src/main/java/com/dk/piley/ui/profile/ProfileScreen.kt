package com.dk.piley.ui.profile

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Upcoming
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.Piley
import com.dk.piley.R
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.nav.Screen
import com.dk.piley.ui.nav.taskScreen
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.ui.viewModelFactory
import com.dk.piley.util.BigSpacer
import com.dk.piley.util.InitialSlideIn
import com.dk.piley.util.MediumSpacer
import com.dk.piley.util.SlideDirection
import com.dk.piley.util.previewUpcomingTasksList


/**
 * Profile screen
 *
 * @param modifier generic modifier
 * @param navController generic navigation controller
 * @param snackbarHostState host state for displaying snackbars
 * @param viewModel profile view model
 */
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: ProfileViewModel = viewModel(
        factory = viewModelFactory {
            ProfileViewModel(
                application = Piley.application,
                pileRepository = Piley.appModule.pileRepository,
                userRepository = Piley.appModule.userRepository
            )
        }
    )
) {
    val viewState by viewModel.state.collectAsState()

    // snackbar handler
    viewState.message?.let { message ->
        LaunchedEffect(message, snackbarHostState) {
            snackbarHostState.showSnackbar(message)
            // reset message
            viewModel.setMessage(null)
        }
    }
    ProfileScreen(
        modifier = modifier,
        viewState = viewState,
        initialTransitionStateValue = false,
        onClickSettings = { navController.navigate(Screen.Settings.route) },
        onUpcomingTaskClick = {
            navController.navigate(taskScreen.root + "/" + it)
        }
    )
}

/**
 * Profile screen content
 *
 * @param modifier generic modifier
 * @param viewState profile view state
 * @param initialTransitionStateValue initial screen content animation transition value
 * @param onClickSettings on click settings
 * @param onUpcomingTaskClick on click upcoming task
 */
@Composable
private fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewState: ProfileViewState,
    initialTransitionStateValue: Boolean = true,
    onClickSettings: () -> Unit = {},
    onUpcomingTaskClick: (Long) -> Unit = {},
) {
    val scrollState = rememberScrollState()

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = LocalDim.current.large,
                    end = LocalDim.current.large,
                    top = LocalDim.current.large
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            InitialSlideIn(
                direction = SlideDirection.RIGHT,
                pathLengthInDp = 40,
                initialTransitionStateValue = initialTransitionStateValue
            ) {
                IconButton(onClick = onClickSettings) {
                    Icon(
                        Icons.Filled.Settings,
                        tint = MaterialTheme.colorScheme.secondary,
                        contentDescription = "go to settings"
                    )
                }
            }
            Spacer(modifier = Modifier.size(LocalDim.current.default))
        }
        InitialSlideIn(
            direction = SlideDirection.DOWN,
            pathLengthInDp = 40,
            initialTransitionStateValue = initialTransitionStateValue
        ) {
            UserInfo(name = viewState.userName)
        }
        InitialSlideIn(
            direction = SlideDirection.UP,
            pathLengthInDp = 20,
            initialTransitionStateValue = initialTransitionStateValue
        ) {
            Column {
                BigSpacer()
                ProfileSection(
                    title = stringResource(R.string.user_statistics_section_title),
                    icon = Icons.Default.BarChart
                ) {
                    TaskStats(
                        doneCount = viewState.doneTasks,
                        deletedCount = viewState.deletedTasks,
                        currentCount = viewState.currentTasks,
                        tasksCompletedPastDays = viewState.tasksCompletedPastDays,
                        biggestPile = viewState.biggestPileName,
                    )
                }
                MediumSpacer()
                ProfileSection(
                    title = stringResource(R.string.upcoming_tasks_section_title),
                    icon = Icons.Default.Upcoming
                ) {
                    UpcomingTasksList(
                        modifier = Modifier.fillMaxWidth(),
                        pileNameTaskList = viewState.upcomingTaskList,
                        onTaskClick = onUpcomingTaskClick
                    )
                }
                MediumSpacer()
            }
        }
    }
}

@PreviewMainScreen
@Composable
fun ProfileScreenPreview() {
    PileyTheme {
        Surface {
            val state = ProfileViewState(
                userName = "Thomas",
                doneTasks = 0,
                deletedTasks = 2,
                currentTasks = 3,
                biggestPileName = "Daily",
                upcomingTaskList = previewUpcomingTasksList,
                tasksCompletedPastDays = 9,
                isLoading = true
            )
            ProfileScreen(viewState = state)
        }
    }
}

@PreviewMainScreen
@Composable
fun ProfileScreenUserOfflinePreview() {
    PileyTheme {
        Surface {
            val state = ProfileViewState(
                userName = "Thomas",
                doneTasks = 0,
                deletedTasks = 2,
                currentTasks = 3,
                upcomingTaskList = previewUpcomingTasksList,
                userIsOffline = true
            )
            ProfileScreen(viewState = state)
        }
    }
}