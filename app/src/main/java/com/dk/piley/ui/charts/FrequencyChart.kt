package com.dk.piley.ui.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.ui.util.lastSevenDays
import org.threeten.bp.LocalDate


@Composable
fun FrequencyChart(
    weekDayFrequencies: List<Int>,
    currentDay: LocalDate,
    modifier: Modifier = Modifier
) {
    val max = weekDayFrequencies.maxOrNull() ?: 0
    val barHeight = 200.dp
    val barWidth = 50.dp

    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier.wrapContentHeight()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
        ) {
            weekDayFrequencies.forEachIndexed { index, value ->
                Box(
                    modifier = Modifier
                        .width(barWidth)
                        .height((value / max.toFloat()) * barHeight)
                        .background(
                            if (index == 6) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.secondary
                            },
                            RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                        )
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = value.toString(),
                        color = if (index == 6) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSecondary
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            lastSevenDays(currentDay).forEachIndexed { index, day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.width(barWidth),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = if (index == 6) FontWeight.Bold else null,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}


@Preview
@Composable
fun FrequencyChartPreview() {
    PileyTheme(useDarkTheme = true) {
        val last7Days = listOf(10, 15, 20, 18, 13, 12, 8)
        FrequencyChart(last7Days, LocalDate.of(2023, 4, 29))
    }
}

