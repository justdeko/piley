package com.dk.piley.ui.pile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.model.task.Task
import com.dk.piley.model.user.PileMode
import com.dk.piley.ui.theme.PileyTheme

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun TaskPile(
    modifier: Modifier = Modifier,
    tasks: List<Task> = emptyList(),
    pileMode: PileMode = PileMode.FREE,
    onDelete: (task: Task) -> Unit = {},
    onDone: (task: Task) -> Unit = {},
    onTaskClick: (task: Task) -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    LazyColumn(
        modifier = modifier.padding(8.dp),
        reverseLayout = true,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom),
    ) {
        itemsIndexed(tasks, key = { _, task -> task.id }) { index, task ->
            // recomposition key of tasks to recalculate possibility of dismiss for last/first item
            val dismissState = remember(tasks) {
                DismissState(DismissValue.Default) {
                    if (cannotDismiss(pileMode, index, tasks.lastIndex)
                        && it == DismissValue.DismissedToEnd
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        false
                    } else true
                }
            }
            if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                onDelete(task)
            } else if (dismissState.isDismissed(DismissDirection.StartToEnd)) {
                onDone(task)
            }
            PileTask(
                Modifier
                    .animateItemPlacement()
                    .padding(vertical = 1.dp),
                dismissState,
                task,
                onClick = onTaskClick
            )
        }
    }
}

fun cannotDismiss(pileMode: PileMode, index: Int, lastIndex: Int) =
    (pileMode == PileMode.FIFO && index != 0) || (pileMode == PileMode.LIFO && index != lastIndex)

@Preview
@Composable
fun DefaultPreview() {
    PileyTheme(useDarkTheme = true) {
        val taskList =
            listOf(
                Task(title = "hey there", id = 1),
                Task(title = "sup", id = 2),
                Task(title = "another task", id = 3),
                Task(title = "fourth task", id = 4),
            )
        TaskPile(tasks = taskList)
    }
}