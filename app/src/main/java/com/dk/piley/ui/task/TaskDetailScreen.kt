package com.dk.piley.ui.task

import RequestNotificationPermissionDialog
import android.Manifest
import android.os.Build
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberBottomDrawerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.R
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.ui.common.EditDescriptionField
import com.dk.piley.ui.common.EditableTitleText
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.previewUpcomingTasksList
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TaskDetailScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val viewState by viewModel.state.collectAsState()
    TaskDetailScreen(
        viewState = viewState,
        onDeleteTask = {
            navController.popBackStack()
            viewModel.deleteTask()
        },
        onCompleteTask = {
            navController.popBackStack()
            viewModel.completeTask()
        },
        onAddReminder = { viewModel.addReminder(it) },
        onCancelReminder = { viewModel.cancelReminder() },
        onClose = { navController.popBackStack() },
        onEditDesc = { viewModel.editDescription(it) },
        onEditTitle = { viewModel.editTitle(it) }
    )
}

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun TaskDetailScreen(
    modifier: Modifier = Modifier,
    viewState: TaskDetailViewState,
    onDeleteTask: () -> Unit = {},
    onCompleteTask: () -> Unit = {},
    onClose: () -> Unit = {},
    onEditDesc: (String) -> Unit = {},
    onEditTitle: (String) -> Unit = {},
    onAddReminder: (ReminderState) -> Unit = {},
    onCancelReminder: () -> Unit = {},
    permissionState: PermissionState? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    },
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)
    val scrollState = rememberScrollState()

    // notification permission
    var rationaleOpen by remember { mutableStateOf(false) }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && drawerState.isOpen) {
        RequestNotificationPermissionDialog(rationaleOpen) {
            rationaleOpen = false
        }
    }

    AddReminderDrawer(
        drawerState = drawerState,
        onAddReminder = onAddReminder,
        onDeleteReminder = onCancelReminder,
        initialDate = viewState.task.reminder,
        isRecurring = viewState.task.isRecurring,
        recurringFrequency = viewState.task.recurringFrequency,
        recurringTimeRange = viewState.task.recurringTimeRange,
        permissionState = permissionState
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                },
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        EditableTitleText(
                            value = viewState.titleTextValue,
                            onValueChange = onEditTitle
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onClose) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                "close the task detail",
                                modifier = Modifier.scale(
                                    1.3F
                                ),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                )
                EditDescriptionField(
                    value = viewState.descriptionTextValue,
                    onChange = {
                        onEditDesc(it)
                    }
                )
                Spacer(Modifier.size(16.dp))
                ReminderInfo(
                    reminderDateTimeText = viewState.reminderDateTimeText,
                    onAddReminder = { scope.launch { drawerState.open() } },
                    isRecurring = viewState.task.isRecurring,
                    recurringTimeRange = viewState.task.recurringTimeRange,
                    recurringFrequency = viewState.task.recurringFrequency
                )
                TaskInfo(
                    modifier = Modifier.fillMaxWidth(),
                    task = viewState.task
                )
            }
            Row(
                modifier = Modifier
                    .weight(1f, false)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onCompleteTask,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Text(text = stringResource(R.string.complete_task_button))
                }
                Button(
                    onClick = onDeleteTask,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text(text = stringResource(R.string.delete_task_button))
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@PreviewMainScreen
@Composable
fun TaskDetailScreenPreview() {
    PileyTheme {
        Surface {
            val state = TaskDetailViewState(
                task = previewUpcomingTasksList[1].second,
                titleTextValue = "Hello"
            )
            TaskDetailScreen(viewState = state, permissionState = null)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@PreviewMainScreen
@Composable
fun TaskDetailScreenPreviewRecurring() {
    PileyTheme {
        Surface {
            val state = TaskDetailViewState(
                task = previewUpcomingTasksList[0].second,
                titleTextValue = "Hello"
            )
            TaskDetailScreen(viewState = state, permissionState = null)
        }
    }
}