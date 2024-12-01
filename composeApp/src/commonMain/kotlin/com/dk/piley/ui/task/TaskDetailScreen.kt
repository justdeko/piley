package com.dk.piley.ui.task

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.Piley
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.ui.common.EditDescriptionField
import com.dk.piley.ui.common.NotificationPermissionHandler
import com.dk.piley.ui.common.TitleTopAppBar
import com.dk.piley.ui.common.TwoButtonRow
import com.dk.piley.ui.common.TwoPaneScreen
import com.dk.piley.ui.reminder.DelayBottomSheet
import com.dk.piley.util.AlertDialogHelper
import com.dk.piley.util.dateTimeString
import com.dk.piley.util.defaultPadding
import com.dk.piley.util.toLocalDateTime
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.complete_recurring_task_dialog_confirm
import piley.composeapp.generated.resources.complete_recurring_task_dialog_description
import piley.composeapp.generated.resources.complete_recurring_task_dialog_title
import piley.composeapp.generated.resources.complete_task_button
import piley.composeapp.generated.resources.delete_task_button
import piley.composeapp.generated.resources.delete_task_dialog_confirm_button
import piley.composeapp.generated.resources.delete_task_dialog_description
import piley.composeapp.generated.resources.delete_task_dialog_title


/**
 * Task detail screen
 *
 * @param navController generic nav controller
 * @param viewModel task detail view model
 */
@Composable
fun TaskDetailScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: TaskDetailViewModel = viewModel {
        TaskDetailViewModel(
            repository = Piley.getModule().taskRepository,
            pileRepository = Piley.getModule().pileRepository,
            taskRepository = Piley.getModule().taskRepository,
            userRepository = Piley.getModule().userRepository,
            reminderManager = Piley.getModule().reminderManager,
            notificationManager = Piley.getModule().notificationManager,
            savedStateHandle = createSavedStateHandle()
        )
    },
    onFinish: () -> Unit = {},
) {
    val viewState by viewModel.state.collectAsState()

    // navigate out of view if task completed or deleted
    if ((viewState.task.status == TaskStatus.DONE && !viewState.task.isRecurring) || viewState.task.status == TaskStatus.DELETED) {
        LaunchedEffect(true) {
            navController.popBackStack()
        }
    }

    // if delay screen was shown and delay action finished
    // trigger onFinish callback to close activity
    if (viewState.delayFinished) {
        LaunchedEffect(Unit) {
            onFinish()
        }
    }

    TaskDetailScreen(
        viewState = viewState,
        onDeleteTask = { viewModel.deleteTask() },
        onCompleteTask = { viewModel.completeTask() },
        onAddReminder = { viewModel.addReminder(it) },
        onCancelReminder = { viewModel.cancelReminder() },
        onClose = { navController.popBackStack() },
        onEditDesc = { viewModel.editDescription(it) },
        onEditTitle = { viewModel.editTitle(it) },
        onSelectPile = { viewModel.selectPile(it) },
        onDelay = { viewModel.delayReminder(it) }
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
 * @param onSelectPile on select pile
 * @param onDelay on set delay
 */
@OptIn(ExperimentalMaterial3Api::class)
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
    onSelectPile: (Int) -> Unit = {},
    onDelay: (Long) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val delaySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDelaySheet by remember { mutableStateOf(false) }
    var completeRecurringDialogOpen by remember { mutableStateOf(false) }
    var confirmDeleteDialogOpen by remember { mutableStateOf(false) }
    var notificationPermissionGranted by remember { mutableStateOf(false) }

    NotificationPermissionHandler(sheetState.hasExpandedState) {
        notificationPermissionGranted = it
    }

    LaunchedEffect(key1 = viewState.showDelaySection) {
        if (viewState.showDelaySection) {
            showDelaySheet = true
        }
    }

    if (completeRecurringDialogOpen) {
        AlertDialogHelper(
            title = stringResource(Res.string.complete_recurring_task_dialog_title),
            description = stringResource(
                Res.string.complete_recurring_task_dialog_description,
                viewState.task.reminder?.toLocalDateTime()?.dateTimeString() ?: ""
            ),
            confirmText = stringResource(Res.string.complete_recurring_task_dialog_confirm),
            onConfirm = {
                onCompleteTask()
                completeRecurringDialogOpen = false
            },
            onDismiss = { completeRecurringDialogOpen = false }
        )
    }

    if (confirmDeleteDialogOpen) {
        AlertDialogHelper(
            title = stringResource(Res.string.delete_task_dialog_title),
            description = stringResource(Res.string.delete_task_dialog_description),
            confirmText = stringResource(Res.string.delete_task_dialog_confirm_button),
            onDismiss = { confirmDeleteDialogOpen = false },
            onConfirm = {
                onDeleteTask()
                confirmDeleteDialogOpen = false
            }
        )
    }

    if (showBottomSheet) {
        AddReminderDrawer(
            sheetState = sheetState,
            onAddReminder = {
                onAddReminder(it)
                scope.launch { sheetState.hide() }
                    .invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
            },
            onDeleteReminder = {
                scope.launch { sheetState.hide() }
                    .invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                            onCancelReminder()
                        }
                    }
            },
            initialDate = viewState.task.reminder?.toLocalDateTime(),
            isRecurring = viewState.task.isRecurring,
            recurringFrequency = viewState.task.recurringFrequency,
            recurringTimeRange = viewState.task.recurringTimeRange,
            onDismiss = { showBottomSheet = false },
            useNowAsReminderDate = viewState.task.nowAsReminderTime,
            notificationPermissionGranted = notificationPermissionGranted
        )
    }

    if (showDelaySheet) {
        DelayBottomSheet(
            defaultDelayRange = viewState.defaultDelayRange,
            defaultDelayIndex = viewState.defaultDelayIndex,
            sheetState = delaySheetState,
            onDelay = {
                onDelay(it)
                showDelaySheet = false
            }
        )
    }

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
        TwoPaneScreen(
            modifier = Modifier.weight(1f),
            mainContent = { isTablet ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
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
                        onAddReminder = { showBottomSheet = true },
                        addReminderButtonEnabled = !isTablet,
                        isRecurring = viewState.task.isRecurring,
                        recurringTimeRange = viewState.task.recurringTimeRange,
                        recurringFrequency = viewState.task.recurringFrequency
                    )
                    TaskInfo(
                        modifier = Modifier.fillMaxWidth(),
                        task = viewState.task
                    )
                    SelectedPile(
                        pileNames = viewState.piles.map { it.second },
                        selectedPileIndex = viewState.selectedPileIndex,
                        onSelect = onSelectPile
                    )
                }
            },
            detailContent = {
                AddReminderContent(
                    modifier = Modifier.fillMaxHeight().verticalScroll(rememberScrollState()),
                    onAddReminder = onAddReminder,
                    onDeleteReminder = onCancelReminder,
                    initialDateTime = viewState.task.reminder?.toLocalDateTime(),
                    isRecurring = viewState.task.isRecurring,
                    recurringTimeRange = viewState.task.recurringTimeRange,
                    recurringFrequency = viewState.task.recurringFrequency,
                    useNowAsReminderTime = viewState.task.nowAsReminderTime,
                    notificationPermissionGranted = notificationPermissionGranted
                )
            }
        )
        TwoButtonRow(
            onLeftClick = { confirmDeleteDialogOpen = true },
            onRightClick = {
                // if task is recurring and reminder is in the future, show dialog first
                if (
                    viewState.task.isRecurring
                    && viewState.task.reminder?.let { it > Clock.System.now() } == true
                ) {
                    completeRecurringDialogOpen = true
                } else {
                    onCompleteTask()
                }
            },
            leftText = stringResource(Res.string.delete_task_button),
            rightText = stringResource(Res.string.complete_task_button),
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
