package com.dk.piley.ui.pile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.Piley
import com.dk.piley.R
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.task.Task
import com.dk.piley.reminder.getNextReminderTime
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.nav.pileScreen
import com.dk.piley.ui.nav.taskScreen
import com.dk.piley.ui.savedStateViewModelFactory
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.dateTimeString
import com.dk.piley.util.getPreviewTransitionStates
import com.dk.piley.util.previewPileWithTasksList
import com.dk.piley.util.previewTaskList
import com.dk.piley.util.previewUpcomingTasksList
import com.dk.piley.util.titleCharacterLimit
import com.dk.piley.util.toLocalDateTime
import kotlinx.coroutines.launch

/**
 * Pile screen
 *
 * @param modifier generic modifier
 * @param navController generic nav controller
 * @param snackbarHostState snackbar host state for displaying snackbars
 * @param viewModel pile view model
 */
@Composable
fun PileScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: PileViewModel = viewModel(
        factory = savedStateViewModelFactory(navController) {
            PileViewModel(
                taskRepository = Piley.appModule.taskRepository,
                pileRepository = Piley.appModule.pileRepository,
                userRepository = Piley.appModule.userRepository,
                savedStateHandle = it
            )
        }
    )
) {
    val context = LocalContext.current
    val viewState by viewModel.state.collectAsState()
    val selectedPileViewState by viewModel.selectedPileIndex.collectAsState()
    // filter out recurring tasks, show if recurring enabled
    val shownTasks = if (viewState.showRecurring) {
        viewState.tasks
    } else {
        viewState.tasks?.filter { !it.isRecurring }
    } ?: emptyList()
    val taskTransitionStates = shownTasks.map {
        remember {
            MutableTransitionState(false).apply {
                targetState = true
            }
        }
    } // TODO fix disappearance animation

    // snackbar handler
    viewState.messageWithAction?.let { message ->
        LaunchedEffect(message, snackbarHostState) {
            val result =
                snackbarHostState.showSnackbar(
                    message = message.message,
                    actionLabel = message.actionText,
                    duration = message.duration
                )
            if (result == SnackbarResult.ActionPerformed) {
                message.action()
            }
            // reset message
            viewModel.setMessage(null)
        }
    }

    PileScreen(
        modifier = modifier,
        shownTasks = shownTasks,
        viewState = viewState,
        taskTransitionStates = taskTransitionStates,
        selectedPileIndex = selectedPileViewState,
        onDone = { viewModel.done(it) },
        onDelete = {
            viewModel.delete(it)
            viewModel.setMessage(
                MessageWithAction(
                    message = context.getString(R.string.task_deleted_message),
                    actionText = context.getString(R.string.undo_task_deleted),
                    duration = SnackbarDuration.Short
                ) { viewModel.undoDelete(it) }
            )
        },
        onAdd = { viewModel.add(it) },
        onClick = { navController.navigate(taskScreen.root + "/" + it.id) },
        onTitlePageChanged = { page -> viewModel.onPileChanged(page) },
        onSetMessage = { viewModel.setMessage(it) },
        onToggleRecurring = { viewModel.setShowRecurring(it) },
        onClickTitle = { navController.navigate(pileScreen.root + "/" + viewState.pile.pileId) }
    )
}

/**
 * Pile screen content
 *
 * @param modifier generic modifier
 * @param viewState pile screen view state
 * @param shownTasks the task list that will be shown inside the pile
 * @param taskTransitionStates animation transition states for pile tasks
 * @param selectedPileIndex index of currently selected pile
 * @param onTitlePageChanged action on pile title change (selection of different pile)
 * @param onDone on task done
 * @param onDelete on task deleted
 * @param onAdd on new task added
 * @param onClick on task click
 * @param onSetMessage on set user message
 * @param onToggleRecurring on toggle show recurring tasks
 * @param onClickTitle on pile title click
 */
@Composable
private fun PileScreen(
    modifier: Modifier = Modifier,
    viewState: PileViewState,
    shownTasks: List<Task> = emptyList(),
    taskTransitionStates: List<MutableTransitionState<Boolean>>,
    selectedPileIndex: Int = 0,
    onTitlePageChanged: (Int) -> Unit = {},
    onDone: (Task) -> Unit = {},
    onDelete: (Task) -> Unit = {},
    onAdd: (String) -> Unit = {},
    onClick: (Task) -> Unit = {},
    onSetMessage: (MessageWithAction) -> Unit = {},
    onToggleRecurring: (Boolean) -> Unit = {},
    onClickTitle: () -> Unit = {},
) {
    val dim = LocalDim.current
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val pileOffset = remember { Animatable(0f) }

    val focusManager = LocalFocusManager.current
    var taskTextValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue("")
        )
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
        verticalArrangement = Arrangement.Bottom
    ) {
        PileTitlePager(
            modifier = Modifier.fillMaxWidth(),
            pileTitleList = viewState.pileIdTitleList.map { it.second },
            onPageChanged = onTitlePageChanged,
            selectedPageIndex = selectedPileIndex,
            onClickTitle = onClickTitle,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            TaskPile(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(pileOffset.value.dp, 0.dp),
                tasks = shownTasks,
                pileMode = viewState.pile.pileMode,
                taskTransitionStates = taskTransitionStates,
                onDone = {
                    // if task is recurring, display completion message and next due date
                    if (it.isRecurring) {
                        onSetMessage(
                            MessageWithAction(
                                context.getString(
                                    R.string.recurring_task_completed_info,
                                    it.getNextReminderTime().toLocalDateTime().dateTimeString()
                                )
                            )
                        )
                    }
                    onDone(it)
                },
                onDelete = onDelete,
                onTaskClick = onClick
            )
            Column(Modifier.fillMaxSize()) {
                AnimatedVisibility(
                    visible = viewState.tasks?.isEmpty() ?: false,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    NoTasksView(
                        Modifier.fillMaxSize(),
                        viewState.noTasksYet
                    )
                }
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(
                    bottom = dim.large,
                    start = dim.large,
                    end = dim.large,
                    top = dim.medium
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AddTaskField(
                modifier = Modifier.weight(1f),
                value = taskTextValue,
                onChange = {
                    if (it.text.length <= titleCharacterLimit) {
                        taskTextValue = it
                    }
                },
                onDone = {
                    if (taskTextValue.text.isNotBlank()) {
                        // if pile limit is not 0 (infinite) and task count above pile limit, don't add
                        if (
                            viewState.pile.pileLimit > 0
                            && (viewState.tasks?.size ?: 0) >= viewState.pile.pileLimit
                        ) {
                            onSetMessage(MessageWithAction(context.getString(R.string.pile_full_warning)))
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            coroutineScope.launch {
                                pileOffset.animateTo(
                                    targetValue = 0f,
                                    animationSpec = shakeAnimationSpec,
                                )
                            }
                        } else {
                            onAdd(taskTextValue.text.trim())
                            if (viewState.autoHideEnabled) {
                                focusManager.clearFocus()
                                defaultKeyboardAction(ImeAction.Done)
                            }
                            taskTextValue = TextFieldValue()
                        }
                    } else {
                        onSetMessage(MessageWithAction(context.getString(R.string.task_empty_not_allowed_hint)))
                    }
                }
            )
            // show recurring tasks filter only if there are recurring tasks
            AnimatedVisibility(viewState.tasks?.any { it.isRecurring } == true) {
                IconToggleButton(
                    checked = viewState.showRecurring,
                    onCheckedChange = onToggleRecurring
                ) {
                    if (viewState.showRecurring) {
                        Icon(
                            Icons.Default.AccessTimeFilled,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "toggle recurring tasks"
                        )
                    } else {
                        Icon(
                            Icons.Outlined.AccessTime,
                            tint = MaterialTheme.colorScheme.secondary,
                            contentDescription = "toggle recurring tasks"
                        )
                    }
                }
            }
        }
    }
}

private val shakeAnimationSpec: AnimationSpec<Float> = keyframes {
    (1..8).forEach { i ->
        when (i % 3) {
            0 -> 8f
            1 -> -8f
            else -> 0f
        } at 500 / 10 * i using FastOutLinearInEasing
    }
}

@PreviewMainScreen
@Composable
fun PileScreenPreview() {
    PileyTheme {
        Surface {
            val pilesWithTasks = previewPileWithTasksList
            val state = PileViewState(
                pile = pilesWithTasks[0].pile,
                tasks = pilesWithTasks[0].tasks,
                pileIdTitleList = pilesWithTasks.map { Pair(it.pile.pileId, it.pile.name) }
            )
            PileScreen(
                viewState = state,
                shownTasks = pilesWithTasks[0].tasks,
                taskTransitionStates = pilesWithTasks[0].tasks.getPreviewTransitionStates()
            )
        }
    }
}

@PreviewMainScreen
@Composable
fun PileScreenRecurringPreview() {
    PileyTheme {
        Surface {
            val pilesWithTasks = previewPileWithTasksList
            val upcomingTasks = previewUpcomingTasksList.map { it.second }
            val state = PileViewState(
                pile = pilesWithTasks[0].pile,
                tasks = upcomingTasks,
                pileIdTitleList = pilesWithTasks.map { Pair(it.pile.pileId, it.pile.name) },
                showRecurring = true
            )
            PileScreen(
                viewState = state,
                shownTasks = upcomingTasks,
                taskTransitionStates = upcomingTasks.getPreviewTransitionStates()
            )
        }
    }
}

@PreviewMainScreen
@Composable
fun PileScreenNoTasksPreview() {
    PileyTheme {
        Surface {
            val state = PileViewState(
                pile = Pile(name = "Daily"),
                tasks = emptyList(),
                pileIdTitleList = listOf(Pair(1, "Pile1"), Pair(2, "Pile2"))
            )
            PileScreen(viewState = state, taskTransitionStates = emptyList())
        }
    }
}

@PreviewMainScreen
@Composable
fun PileScreenManyTasksPreview() {
    PileyTheme {
        Surface {
            val pilesWithTasks = previewPileWithTasksList
            val state = PileViewState(
                pile = pilesWithTasks[0].pile,
                tasks = previewTaskList,
                pileIdTitleList = pilesWithTasks.map { Pair(it.pile.pileId, it.pile.name) }
            )
            PileScreen(
                viewState = state,
                taskTransitionStates = previewTaskList.getPreviewTransitionStates(),
                shownTasks = previewTaskList
            )
        }
    }
}