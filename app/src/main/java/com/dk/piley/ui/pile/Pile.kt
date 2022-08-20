package com.dk.piley.ui.pile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.model.task.Task
import com.dk.piley.ui.theme.PileyTheme

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun Pile(
    modifier: Modifier = Modifier,
    tasks: List<Task> = emptyList(),
    onDismiss: (task: Task) -> Unit
) {
    LazyColumn(
        modifier = modifier.padding(8.dp),
        reverseLayout = true,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom),
    ) {
        items(tasks, key = { it.id }) { task ->
            val dismissState = rememberDismissState()
            if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                onDismiss(task)
            } else if (dismissState.isDismissed(DismissDirection.StartToEnd)) {
                onDismiss(task)
            }
            PileTask(
                Modifier
                    .animateItemPlacement()
                    .padding(vertical = 1.dp),
                dismissState,
                task
            )
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    PileyTheme(useDarkTheme = true) {
        val taskList =
            listOf(
                Task(title = "hey there", id = 0),
                Task(title = "sup", id = 1),
                Task(title = "another task", id = 2),
                Task(title = "fourth task", id = 3),
            )
        Pile(tasks = taskList, onDismiss = {})
    }
}