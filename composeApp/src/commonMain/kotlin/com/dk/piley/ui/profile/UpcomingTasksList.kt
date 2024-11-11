package com.dk.piley.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.dk.piley.model.task.Task
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.util.MediumSpacer
import com.dk.piley.util.dateTimeStringNewLine
import com.dk.piley.util.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.no_upcoming_tasks_hint

/**
 * List of tasks with upcoming reminders
 *
 * @param modifier generic modifier
 * @param pileNameTaskList list of pile name-task pairs
 * @param onTaskClick action on task click, passes task id
 */
@Composable
fun UpcomingTasksList(
    modifier: Modifier = Modifier,
    pileNameTaskList: List<Pair<String, Task>>,
    onTaskClick: (Long) -> Unit = {}
) {
    val dim = LocalDim.current
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (pileNameTaskList.isNotEmpty()) {
            pileNameTaskList.forEachIndexed { index, (pileName, task) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTaskClick(task.id) }
                        .padding(horizontal = dim.large, vertical = dim.small),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(2f)) {
                        Text(
                            text = task.title,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Start
                        )
                        Text(
                            text = pileName,
                            color = MaterialTheme.colorScheme.tertiary,
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Start
                        )
                    }
                    MediumSpacer()
                    Text(
                        modifier = Modifier.weight(1f),
                        text = task.reminder?.toLocalDateTime()?.dateTimeStringNewLine() ?: "",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.End
                    )
                }
                if (index < pileNameTaskList.lastIndex) {
                    HorizontalDivider(Modifier.padding(horizontal = dim.large))
                }
            }
        } else {
            Text(
                modifier = Modifier.padding(horizontal = dim.large, vertical = dim.small),
                text = stringResource(Res.string.no_upcoming_tasks_hint),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
