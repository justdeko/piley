package com.dk.piley.ui.pile

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import com.dk.piley.model.task.Task
import com.dk.piley.model.user.PileMode
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.util.AlertDialogHelper
import com.dk.piley.util.dateTimeString
import com.dk.piley.util.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.complete_recurring_task_dialog_confirm
import piley.composeapp.generated.resources.complete_recurring_task_dialog_description
import piley.composeapp.generated.resources.complete_recurring_task_dialog_title
import piley.composeapp.generated.resources.delete_recurring_dialog_confirm
import piley.composeapp.generated.resources.delete_recurring_dialog_description
import piley.composeapp.generated.resources.delete_recurring_dialog_title
import kotlin.time.Clock

/**
 * Task pile view
 *
 * @param modifier generic modifier
 * @param tasks task list
 * @param pileMode pile mode of the pile
 * @param onDelete on task delete
 * @param onDone on task done
 * @param onTaskClick on task click
 * @param onTaskLongPress on task long press
 */
@Composable
fun TaskPile(
    modifier: Modifier = Modifier,
    tasks: List<Task> = emptyList(),
    pileMode: PileMode = PileMode.FREE,
    onDelete: (task: Task) -> Unit = {},
    onDone: (task: Task) -> Unit = {},
    onTaskClick: (task: Task) -> Unit = {},
    onTaskLongPress: (task: Task) -> Unit = {},
) {
    val haptic = LocalHapticFeedback.current
    val dim = LocalDim.current
    val density = LocalDensity.current
    var recurringTaskToComplete by rememberSaveable { mutableStateOf<Task?>(null) }
    if (recurringTaskToComplete != null) {
        AlertDialogHelper(
            title = stringResource(Res.string.complete_recurring_task_dialog_title),
            description = stringResource(
                Res.string.complete_recurring_task_dialog_description,
                recurringTaskToComplete?.reminder?.toLocalDateTime()?.dateTimeString() ?: ""
            ),
            confirmText = stringResource(Res.string.complete_recurring_task_dialog_confirm),
            onConfirm = {
                recurringTaskToComplete?.let {
                    onDone(it)
                }
                recurringTaskToComplete = null
            },
            onDismiss = { recurringTaskToComplete = null }
        )
    }
    var recurringTaskToDelete by rememberSaveable { mutableStateOf<Task?>(null) }
    recurringTaskToDelete?.let { task ->
        AlertDialogHelper(
            title = stringResource(Res.string.delete_recurring_dialog_title),
            description = stringResource(Res.string.delete_recurring_dialog_description),
            confirmText = stringResource(Res.string.delete_recurring_dialog_confirm),
            onDismiss = { recurringTaskToDelete = null },
            onConfirm = {
                onDelete(task)
                recurringTaskToDelete = null
            }
        )
    }
    LazyColumn(
        modifier = modifier.padding(dim.medium),
        reverseLayout = true,
        verticalArrangement = Arrangement.spacedBy(dim.small, Alignment.Bottom),
    ) {
        itemsIndexed(tasks, key = { _, task -> task.id }) { index, task ->
            // recomposition key of tasks to recalculate possibility of dismiss for last/first item
            val dismissState = remember(tasks) {
                SwipeToDismissBoxState(
                    initialValue = SwipeToDismissBoxValue.Settled,
                    density = density,
                    confirmValueChange = { swipeToDismissBoxValue ->
                        if (cannotDismiss(pileMode, index, tasks.lastIndex)
                            && swipeToDismissBoxValue == SwipeToDismissBoxValue.StartToEnd
                        ) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            false
                        }
                        // if task is recurring and reminder not shown yet, show premature completion dialog
                        else if (
                            task.isRecurring
                            && task.reminder?.let { it > Clock.System.now() } == true
                            && swipeToDismissBoxValue == SwipeToDismissBoxValue.StartToEnd
                        ) {
                            recurringTaskToComplete = task
                            false
                        } else if (task.isRecurring && swipeToDismissBoxValue == SwipeToDismissBoxValue.EndToStart) {
                            recurringTaskToDelete = task
                            false
                        } else true
                    },
                    positionalThreshold = { it }
                )
            }
            if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete(task)
            } else if (dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd) {
                onDone(task)
            }
            PileTask(
                modifier = Modifier
                    .animateItem(
                        fadeInSpec = spring(
                            stiffness = Spring.StiffnessMediumLow,
                            visibilityThreshold = 1f
                        )
                    )
                    .padding(vertical = dim.mini),
                dismissState = dismissState,
                task = task,
                onClick = onTaskClick,
                onLongPress = onTaskLongPress,
            )
        }
    }
}

fun cannotDismiss(pileMode: PileMode, index: Int, lastIndex: Int) =
    (pileMode == PileMode.FIFO && index != 0) || (pileMode == PileMode.LIFO && index != lastIndex)
