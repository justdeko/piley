package com.dk.piley.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.R
import com.dk.piley.ui.common.FullWidthInfo
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.MediumSpacer

/**
 * Task statistics
 *
 * @param modifier generic modifier
 * @param doneCount completed task count
 * @param deletedCount deleted task count
 * @param currentCount current task count
 * @param averageTaskDuration average task completion duration
 * @param biggestPile name of the pile with the largest amount of tasks
 * @param tasksOnly whether only statistics about the tasks themselves should be shown
 */
@Composable
fun TaskStats(
    modifier: Modifier = Modifier,
    doneCount: Int,
    deletedCount: Int,
    currentCount: Int,
    tasksCompletedPastDays: Int = 0,
    biggestPile: String = "",
    tasksOnly: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(LocalDim.current.large)
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
            MediumSpacer()
            FullWidthInfo(
                label = stringResource(R.string.tasks_completed_past_days_label),
                value = tasksCompletedPastDays.toString()
            )
            FullWidthInfo(
                label = stringResource(R.string.biggest_pile_label),
                value = biggestPile
            )
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
            tasksCompletedPastDays = 4,
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