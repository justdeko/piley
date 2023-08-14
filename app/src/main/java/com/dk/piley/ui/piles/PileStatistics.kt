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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.R
import com.dk.piley.ui.charts.FrequencyChart
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.common.OutlineCard
import com.dk.piley.ui.common.TitleHeader
import com.dk.piley.ui.profile.TaskStats
import com.dk.piley.ui.theme.PileyTheme
import java.time.LocalDate

@Composable
fun PileStatistics(
    modifier: Modifier = Modifier,
    doneCount: Int = 0,
    deletedCount: Int = 0,
    currentCount: Int = 0,
    completedTaskCounts: List<Int> = emptyList(),
    currentDay: LocalDate = LocalDate.now(),
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
                title = stringResource(R.string.statistics_section_title),
                icon = Icons.Default.BarChart
            )
            TextButton(
                onClick = onClearStatistics,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text(stringResource(R.string.clear_statistics_button_text))
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

@Preview
@Composable
fun PileStatisticsEmptyPreview() {
    PileyTheme(useDarkTheme = true) {
        PileStatistics()
    }
}

@Preview
@Composable
fun PileStatisticsPreview() {
    PileyTheme(useDarkTheme = true) {
        PileStatistics(
            completedTaskCounts = listOf(2, 3, 0, 0, 2, 3, 4),
            doneCount = 2,
            deletedCount = 4,
            currentCount = 1
        )
    }
}