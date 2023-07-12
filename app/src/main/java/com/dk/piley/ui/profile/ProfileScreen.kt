package com.dk.piley.ui.profile

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.ui.nav.Screen
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.AlertDialogHelper
import com.dk.piley.util.IndefiniteProgressBar
import com.dk.piley.util.navigateClearBackstack
import com.jakewharton.threetenabp.AndroidThreeTen
import org.threeten.bp.LocalDateTime


@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val viewState by viewModel.state.collectAsState()
    val context = LocalContext.current

    if (viewState.signedOutState == SignOutState.SIGNED_OUT) {
        LaunchedEffect(true) {
            navController.navigateClearBackstack(Screen.SignIn.route)
        }
    }
    if (viewState.toastMessage != null) {
        LaunchedEffect(key1 = viewState.toastMessage) {
            Toast.makeText(context, viewState.toastMessage, Toast.LENGTH_SHORT).show()
            viewModel.setToastMessage(null)
        }
    }
    ProfileScreen(modifier = modifier,
        viewState = viewState,
        setSignOutState = { viewModel.setSignedOutState(it) },
        onClickSettings = { navController.navigate(Screen.Settings.route) },
        onBackup = { viewModel.attemptBackup() },
        onSignOut = {
            viewModel.signOut()
        },
        onSignOutWithError = {
            viewModel.signOutAfterError()
        }
    )
}

@Composable
private fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewState: ProfileViewState,
    setSignOutState: (state: SignOutState) -> Unit = {},
    onClickSettings: () -> Unit = {},
    onBackup: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onSignOutWithError: () -> Unit = {},
) {
    val scrollState = rememberScrollState()
    if (viewState.signedOutState == SignOutState.SIGNED_OUT_ERROR) {
        AlertDialogHelper(
            title = "Error when uploading backup",
            description = "An error occurred when uploading the backup. Do you still want to sign out? Recent changes might be lost.",
            confirmText = "Sign out",
            onConfirm = onSignOutWithError,
            onDismiss = { setSignOutState(SignOutState.SIGNED_IN) }
        )
    }
    Box(modifier = modifier.fillMaxSize()) {
        IndefiniteProgressBar(visible = viewState.signedOutState == SignOutState.SIGNING_OUT || viewState.showProgressBar)
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
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
                if (viewState.userIsOffline) {
                    Spacer(modifier = Modifier.size(0.dp))
                } else {
                    IconButton(onClick = onSignOut) {
                        Icon(
                            Icons.Filled.Logout,
                            tint = MaterialTheme.colorScheme.secondary,
                            contentDescription = "sign out"
                        )
                    }
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
                currentCount = viewState.currentTasks,
                averageTaskDuration = viewState.averageTaskDurationInHours,
                biggestPile = viewState.biggestPileName,
            )
            Text(
                text = "Upcoming Tasks",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 16.dp),
                textAlign = TextAlign.Start
            )
            UpcomingTasksList(
                modifier = Modifier.fillMaxWidth(),
                pileNameTaskList = viewState.upcomingTaskList
            )
            if (!viewState.userIsOffline) {
                Text(
                    text = "Backup",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                    textAlign = TextAlign.Start
                )
                BackupInfo(lastBackup = viewState.lastBackup, onClickBackup = onBackup)
            }
            Box(Modifier.weight(1f), contentAlignment = Alignment.BottomCenter) {
                AppInfo()
            }
        }
    }
}

@PreviewMainScreen
@Composable
fun ProfileScreenPreview() {
    AndroidThreeTen.init(LocalContext.current)
    PileyTheme {
        Surface {
            val state = ProfileViewState(
                userName = "Thomas",
                lastBackup = LocalDateTime.now(),
                doneTasks = 0,
                deletedTasks = 2,
                currentTasks = 3,
                upcomingTaskList = previewUpcomingTasksList
            )
            ProfileScreen(viewState = state)
        }
    }
}