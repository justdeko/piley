package com.dk.piley.ui.task

import android.Manifest
import android.os.Build
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomDrawerState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.R
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.ui.common.EditDescriptionField
import com.dk.piley.ui.common.TitleTopAppBar
import com.dk.piley.ui.common.TwoButtonRow
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.RequestNotificationPermissionDialog
import com.dk.piley.util.defaultPadding
import com.dk.piley.util.previewUpcomingTasksList
import com.dk.piley.util.toLocalDateTime
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

/**
 * Task detail screen
 *
 * @param navController generic nav controller
 * @param viewModel task detail view model
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TaskDetailScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val viewState by viewModel.state.collectAsState()

    // navigate out of view if task completed or deleted
    if (viewState.task.status == TaskStatus.DONE || viewState.task.status == TaskStatus.DELETED) {
        LaunchedEffect(true) {
            navController.popBackStack()
        }
    } // TODO don't do that for done recurring tasks

    TaskDetailScreen(
        viewState = viewState,
        onDeleteTask = { viewModel.deleteTask() },
        onCompleteTask = { viewModel.completeTask() },
        onAddReminder = { viewModel.addReminder(it) },
        onCancelReminder = { viewModel.cancelReminder() },
        onClose = { navController.popBackStack() },
        onEditDesc = { viewModel.editDescription(it) },
        onEditTitle = { viewModel.editTitle(it) }
    )
}

/**
 * Task detail screen content
 *
 * @param modifier generic modifier
 * @param viewState task detail view state
 * @param onDeleteTask on delete task click
 * @param onCompleteTask on complete task click
 * @param onClose on close task detail screen
 * @param onEditDesc on edit task description
 * @param onEditTitle on edit task title
 * @param onAddReminder on add reminder click
 * @param onCancelReminder on cancel reminder action
 * @param permissionState notification permission state
 */
@OptIn(
    ExperimentalMaterialApi::class,
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
        initialDate = viewState.task.reminder?.toLocalDateTime(),
        isRecurring = viewState.task.isRecurring,
        recurringFrequency = viewState.task.recurringFrequency,
        recurringTimeRange = viewState.task.recurringTimeRange,
        permissionState = permissionState
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .defaultPadding()
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
                TitleTopAppBar(
                    textValue = viewState.titleTextValue,
                    canDeleteOrEdit = true,
                    onEdit = onEditTitle,
                    contentDescription = "close the task detail",
                    onButtonClick = onClose
                )
                EditDescriptionField(
                    value = viewState.descriptionTextValue,
                    onChange = {
                        onEditDesc(it)
                    }
                )
                ReminderInfo(
                    reminderDateTimeText = viewState.reminderDateTimeText,
                    onAddReminder = { scope.launch { drawerState.expand() } },
                    isRecurring = viewState.task.isRecurring,
                    recurringTimeRange = viewState.task.recurringTimeRange,
                    recurringFrequency = viewState.task.recurringFrequency
                )
                TaskInfo(
                    modifier = Modifier.fillMaxWidth(),
                    task = viewState.task
                )
            }
            TwoButtonRow(
                modifier = Modifier.weight(1f, false),
                onRightClick = onCompleteTask,
                onLeftClick = onDeleteTask,
                rightText = stringResource(R.string.complete_task_button),
                leftText = stringResource(R.string.delete_task_button),
                arrangement = Arrangement.SpaceEvenly,
                leftColors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                rightColors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
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