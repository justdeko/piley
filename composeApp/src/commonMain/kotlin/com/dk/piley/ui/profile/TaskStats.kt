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
import androidx.compose.ui.text.TextStyle
import com.dk.piley.ui.charts.FrequencyChart
import com.dk.piley.ui.common.FullWidthInfo
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.util.MediumSpacer
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.biggest_pile_label
import piley.composeapp.generated.resources.current_tasks_label
import piley.composeapp.generated.resources.done_tasks_label
import piley.composeapp.generated.resources.recurring_tasks_label
import piley.composeapp.generated.resources.tasks_completed_past_days_label

/**
 * Task statistics
 *
 * @param modifier generic modifier
 * @param doneCount completed task count
 * @param recurringCount recurring task count
 * @param currentCount current task count
 * @param tasksCompletedPastDays number of tasks completed in the last n days
 * @param biggestPile name of the pile with the largest amount of tasks
 * @param tasksOnly whether only statistics about the tasks themselves should be shown
 * @param chartFrequencies list of task completion frequencies for the past 7 days, if null, no chart will be shown
 */
@Composable
fun TaskStats(
    modifier: Modifier = Modifier,
    doneCount: Int,
    recurringCount: Int,
    currentCount: Int,
    tasksCompletedPastDays: Int = 0,
    biggestPile: String = "",
    tasksOnly: Boolean = false,
    chartFrequencies: List<Int>? = null
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(LocalDim.current.large)
        ) {
            StatsColumn(
                description = stringResource(Res.string.done_tasks_label),
                content = doneCount.toString()
            )
            StatsColumn(
                description = stringResource(Res.string.current_tasks_label),
                content = currentCount.toString(),
                contentStyle = MaterialTheme.typography.headlineLarge
            )
            StatsColumn(
                description = stringResource(Res.string.recurring_tasks_label),
                content = recurringCount.toString()
            )
        }
        if (!tasksOnly) {
            MediumSpacer()
            FullWidthInfo(
                label = stringResource(Res.string.tasks_completed_past_days_label),
                value = tasksCompletedPastDays.toString()
            )
            MediumSpacer()
            FullWidthInfo(
                label = stringResource(Res.string.biggest_pile_label),
                value = biggestPile
            )
        }
        chartFrequencies?.let {
            MediumSpacer()
            FrequencyChart(weekDayFrequencies = it)
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
