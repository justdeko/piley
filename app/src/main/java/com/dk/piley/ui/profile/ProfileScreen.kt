package com.dk.piley.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Cloud
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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.R
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.nav.Screen
import com.dk.piley.ui.nav.taskScreen
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.AlertDialogHelper
import com.dk.piley.util.BigSpacer
import com.dk.piley.util.IndefiniteProgressBar
import com.dk.piley.util.InitialSlideIn
import com.dk.piley.util.MediumSpacer
import com.dk.piley.util.SlideDirection
import com.dk.piley.util.navigateClearBackstack
import com.dk.piley.util.previewUpcomingTasksList
import java.time.LocalDateTime


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
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val viewState by viewModel.state.collectAsState()
    // sign out handler
    if (viewState.signedOutState == SignOutState.SIGNED_OUT) {
        LaunchedEffect(true) {
            navController.navigateClearBackstack(Screen.SignIn.route)
        }
    }
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
        setSignOutState = { viewModel.setSignedOutState(it) },
        initialTransitionStateValue = false,
        onClickSettings = { navController.navigate(Screen.Settings.route) },
        onBackup = { viewModel.attemptBackup() },
        onSignOut = {
            viewModel.signOut()
        },
        onSignOutWithError = {
            viewModel.signOutAfterError()
        },
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
 * @param setSignOutState on set sign out state
 * @param initialTransitionStateValue initial screen content animation transition value
 * @param onClickSettings on click settings
 * @param onBackup on click backup action
 * @param onSignOut on click sign out
 * @param onSignOutWithError on click sign out after showing sign out error
 * @param onUpcomingTaskClick on click upcoming task
 */
@Composable
private fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewState: ProfileViewState,
    setSignOutState: (state: SignOutState) -> Unit = {},
    initialTransitionStateValue: Boolean = true,
    onClickSettings: () -> Unit = {},
    onBackup: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onSignOutWithError: () -> Unit = {},
    onUpcomingTaskClick: (Long) -> Unit = {},
) {
    val scrollState = rememberScrollState()

    if (viewState.signedOutState == SignOutState.SIGNED_OUT_ERROR) {
        AlertDialogHelper(
            title = stringResource(R.string.backup_error_dialog_title),
            description = stringResource(R.string.backup_error_dialog_description),
            confirmText = stringResource(R.string.backup_error_dialog_confirm_button),
            onConfirm = onSignOutWithError,
            onDismiss = { setSignOutState(SignOutState.SIGNED_IN) }
        )
    }
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            Modifier
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
                if (viewState.userIsOffline) {
                    Spacer(modifier = Modifier.size(LocalDim.current.default))
                } else {
                    InitialSlideIn(
                        direction = SlideDirection.LEFT,
                        pathLengthInDp = 40,
                        initialTransitionStateValue = initialTransitionStateValue
                    ) {
                        IconButton(onClick = onSignOut) {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                tint = MaterialTheme.colorScheme.secondary,
                                contentDescription = "sign out"
                            )
                        }
                    }
                }
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
                            averageTaskDuration = viewState.averageTaskDurationInHours,
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
                    if (!viewState.userIsOffline) {
                        MediumSpacer()
                        ProfileSection(
                            title = stringResource(R.string.backup_section_title),
                            icon = Icons.Default.Cloud
                        ) {
                            BackupInfo(lastBackup = viewState.lastBackup, onClickBackup = onBackup)
                        }
                    }
                    MediumSpacer()
                }
            }
        }
        IndefiniteProgressBar(visible = viewState.signedOutState == SignOutState.SIGNING_OUT || viewState.isLoading)
    }
}

@PreviewMainScreen
@Composable
fun ProfileScreenPreview() {
    PileyTheme {
        Surface {
            val state = ProfileViewState(
                userName = "Thomas",
                lastBackup = LocalDateTime.now(),
                doneTasks = 0,
                deletedTasks = 2,
                currentTasks = 3,
                biggestPileName = "Daily",
                upcomingTaskList = previewUpcomingTasksList,
                averageTaskDurationInHours = 9,
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
                lastBackup = LocalDateTime.now(),
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