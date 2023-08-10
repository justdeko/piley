package com.dk.piley.ui.piles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.R
import com.dk.piley.ui.charts.FrequencyChart
import com.dk.piley.ui.common.OutlineCard
import com.dk.piley.ui.common.TitleHeader
import com.dk.piley.ui.profile.TaskStats
import com.dk.piley.ui.theme.PileyTheme
import com.jakewharton.threetenabp.AndroidThreeTen
import org.threeten.bp.LocalDate

@Composable
fun PileStatistics(
    modifier: Modifier = Modifier,
    doneCount: Int = 0,
    deletedCount: Int = 0,
    currentCount: Int = 0,
    completedTaskCounts: List<Int> = emptyList(),
    currentDay: LocalDate = LocalDate.now(),
    onClearStatistics: () -> Unit = {}
) {
    OutlineCard(modifier.padding(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TitleHeader(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f),
                title = stringResource(R.string.statistics_section_title),
                icon = Icons.Default.BarChart
            )
            TextButton(onClick = onClearStatistics) {
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
            modifier = Modifier.padding(horizontal = 16.dp),
            weekDayFrequencies = completedTaskCounts,
            currentDay = currentDay
        )
    }
}

@Preview
@Composable
fun PileStatisticsEmptyPreview() {
    AndroidThreeTen.init(LocalContext.current)
    PileyTheme(useDarkTheme = true) {
        PileStatistics()
    }
}

@Preview
@Composable
fun PileStatisticsPreview() {
    AndroidThreeTen.init(LocalContext.current)
    PileyTheme(useDarkTheme = true) {
        PileStatistics(
            completedTaskCounts = listOf(2, 3, 0, 0, 2, 3, 4),
            doneCount = 2,
            deletedCount = 4,
            currentCount = 1
        )
    }
}