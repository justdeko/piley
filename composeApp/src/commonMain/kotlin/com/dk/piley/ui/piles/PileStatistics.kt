package com.dk.piley.ui.piles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dk.piley.ui.charts.FrequencyChart
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.common.OutlineCard
import com.dk.piley.ui.common.TitleHeader
import com.dk.piley.ui.profile.TaskStats
import com.dk.piley.util.toLocalDateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.clear_statistics_button_text
import piley.composeapp.generated.resources.statistics_section_title

/**
 * Pile statistics section
 *
 * @param modifier generic modifier
 * @param doneCount completed tasks in pile count
 * @param deletedCount deleted tasks in pile count
 * @param currentCount current tasks in pile count
 * @param completedTaskCounts 7-day list of completed task frequencies
 * @param currentDay current date of today
 * @param clearStatisticsVisible whether the clear statistics button is visible
 * @param onClearStatistics on clear pile statistics
 * @param initialGraphTransitionValue initial graph animation transition value
 */
@Composable
fun PileStatistics(
    modifier: Modifier = Modifier,
    doneCount: Int = 0,
    deletedCount: Int = 0,
    currentCount: Int = 0,
    completedTaskCounts: List<Int> = emptyList(),
    currentDay: LocalDate = Clock.System.now().toLocalDateTime().date,
    clearStatisticsVisible: Boolean = true,
    onClearStatistics: () -> Unit = {},
    initialGraphTransitionValue: Boolean = true
) {
    val dim = LocalDim.current
    OutlineCard(modifier.padding(dim.medium)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TitleHeader(
                modifier = Modifier
                    .padding(horizontal = dim.medium)
                    .weight(1f),
                title = stringResource(Res.string.statistics_section_title),
                icon = Icons.Default.BarChart
            )
            if (clearStatisticsVisible) {
                TextButton(
                    onClick = onClearStatistics,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text(stringResource(Res.string.clear_statistics_button_text))
                }
            }
        }
        TaskStats(
            doneCount = doneCount,
            deletedCount = deletedCount,
            currentCount = currentCount,
            tasksOnly = true
        )
        FrequencyChart(
            modifier = Modifier.padding(horizontal = dim.large),
            weekDayFrequencies = completedTaskCounts,
            currentDay = currentDay,
            initialTransitionValue = initialGraphTransitionValue
        )
    }
}
