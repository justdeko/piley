package com.dk.piley.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.Piley
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.common.TwoPaneScreen
import com.dk.piley.ui.nav.Screen
import com.dk.piley.ui.nav.taskScreen
import com.dk.piley.util.BigSpacer
import com.dk.piley.util.InitialSlideIn
import com.dk.piley.util.MediumSpacer
import com.dk.piley.util.SlideDirection
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.no_pile
import piley.composeapp.generated.resources.user_statistics_section_title


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
    viewModel: ProfileViewModel = viewModel {
        ProfileViewModel(
            pileRepository = Piley.getModule().pileRepository,
            userRepository = Piley.getModule().userRepository
        )
    }
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
        onClickSync = { navController.navigate(Screen.Sync.route) },
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
 * @param onClickSync on click sync
 * @param onUpcomingTaskClick on click upcoming task
 */
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewState: ProfileViewState,
    initialTransitionStateValue: Boolean = true,
    onClickSettings: () -> Unit = {},
    onClickSync: () -> Unit = {},
    onUpcomingTaskClick: (Long) -> Unit = {},
) {
    val scrollState = rememberScrollState()
    TwoPaneScreen(
        modifier = modifier,
        mainContent = { isTabletWide ->
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                InitialSlideIn(
                    direction = SlideDirection.DOWN,
                    pathLengthInDp = 40,
                    initialTransitionStateValue = initialTransitionStateValue
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                start = LocalDim.current.large,
                                end = LocalDim.current.large,
                                top = LocalDim.current.large
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UserInfo(
                            modifier = Modifier.weight(1f),
                            name = viewState.userName
                        )
                        IconButton(onClick = onClickSettings) {
                            Icon(
                                Icons.Filled.Settings,
                                tint = MaterialTheme.colorScheme.secondary,
                                contentDescription = "go to settings"
                            )
                        }
                        IconButton(onClick = onClickSync) {
                            Icon(
                                Icons.Filled.Sync,
                                tint = MaterialTheme.colorScheme.secondary,
                                contentDescription = "go to sync"
                            )
                        }
                    }
                }
                InitialSlideIn(
                    direction = SlideDirection.UP,
                    pathLengthInDp = 20,
                    initialTransitionStateValue = initialTransitionStateValue
                ) {
                    Column {
                        BigSpacer()
                        ProfileSection(
                            title = stringResource(Res.string.user_statistics_section_title),
                            icon = Icons.Default.BarChart
                        ) {
                            TaskStats(
                                doneCount = viewState.doneTasks,
                                recurringCount = viewState.recurringTasks,
                                currentCount = viewState.currentTasks,
                                tasksCompletedPastDays = viewState.tasksCompletedPastDays,
                                biggestPile = viewState.biggestPileName
                                    ?: stringResource(Res.string.no_pile),
                                chartFrequencies = if (isTabletWide) viewState.completedTaskFrequencies else null
                            )
                        }
                        MediumSpacer()
                        if (!isTabletWide) {
                            UpcomingTasksSection(
                                modifier = Modifier.fillMaxWidth(),
                                pileNameTaskList = viewState.upcomingTaskList,
                                onTaskClick = onUpcomingTaskClick
                            )
                            MediumSpacer()
                        }
                    }
                }
            }
        }, detailContent = {
            Column(
                Modifier
                    .padding(top = LocalDim.current.large)
                    .verticalScroll(rememberScrollState())
            ) {
                UpcomingTasksSection(
                    modifier = Modifier.fillMaxWidth(),
                    pileNameTaskList = viewState.upcomingTaskList,
                    onTaskClick = onUpcomingTaskClick
                )
            }
        }
    )
}
