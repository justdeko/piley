package com.dk.piley.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.R
import com.dk.piley.ui.theme.PileyTheme

@Composable
fun TaskStats(
    modifier: Modifier = Modifier,
    doneCount: Int,
    deletedCount: Int,
    currentCount: Int,
    averageTaskDuration: Long = 0,
    biggestPile: String = "",
    tasksOnly: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatsColumn(
                description = stringResource(R.string.done_tasks_label),
                content = doneCount.toString()
            )
            StatsColumn(
                description = stringResource(R.string.current_tasks_label),
                content = currentCount.toString(),
                contentStyle = MaterialTheme.typography.headlineLarge
            )
            StatsColumn(
                description = stringResource(R.string.deleted_tasks_label),
                content = deletedCount.toString()
            )
        }
        if (!tasksOnly) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.average_task_completion_duration_label),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(R.string.average_task_completion_duration_value, averageTaskDuration),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.biggest_pile_label),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = biggestPile,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun RowScope.StatsColumn(
    modifier: Modifier = Modifier,
    description: String,
    content: String,
    contentStyle: TextStyle = MaterialTheme.typography.headlineMedium,
) {
    Box(modifier = modifier.weight(1f), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = description,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = content,
                style = contentStyle,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Preview
@Composable
fun TaskStatsPreview() {
    PileyTheme(useDarkTheme = true) {
        TaskStats(
            modifier = Modifier.fillMaxWidth(),
            doneCount = 2,
            deletedCount = 3,
            currentCount = 1,
            averageTaskDuration = 4,
            biggestPile = "Home",
        )
    }
}

@Preview
@Composable
fun TaskStatsTasksOnlyPreview() {
    PileyTheme(useDarkTheme = true) {
        TaskStats(
            modifier = Modifier.fillMaxWidth(),
            doneCount = 2,
            deletedCount = 3,
            currentCount = 1,
            tasksOnly = true
        )
    }
}