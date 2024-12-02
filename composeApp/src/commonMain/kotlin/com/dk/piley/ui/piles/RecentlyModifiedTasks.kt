package com.dk.piley.ui.piles

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.dk.piley.model.task.Task
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.common.OutlineCard
import com.dk.piley.util.dateTimeString
import com.dk.piley.util.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.recently_deleted_section_title

// TODO make this animated using either AnimatedVisibility or LazyColumn
@Composable
fun RecentlyModifiedTasks(
    modifier: Modifier = Modifier,
    tasks: List<Task>,
    onUndo: (Task) -> Unit = {}
) {
    val dim = LocalDim.current
    if (tasks.isNotEmpty()) {
        OutlineCard(modifier.padding(dim.medium)) {
            Column(Modifier.fillMaxWidth().padding(dim.small)) {
                Text(
                    text = stringResource(Res.string.recently_deleted_section_title),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Start
                )
                tasks.forEachIndexed { index, task ->
                    RecentlyModifiedTaskItem(
                        index = index,
                        task = task,
                        onUndo = onUndo
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentlyModifiedTaskItem(
    modifier: Modifier = Modifier,
    index: Int,
    task: Task,
    onUndo: (Task) -> Unit
) {
    Column(modifier) {
        if (index > 0) {
            HorizontalDivider(Modifier.padding(LocalDim.current.medium))
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = task.modifiedAt.toLocalDateTime().dateTimeString(),
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Start
                )
            }
            IconButton(
                onClick = { onUndo(task) },
                content = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "undo"
                    )
                }
            )
        }
    }
}