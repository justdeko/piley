package com.dk.piley.ui.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.lastSevenDays
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
        Box(contentAlignment = Alignment.Center) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = barHeight)
            ) {
                weekDayFrequencies.forEachIndexed { index, value ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentHeight(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        val boxHeight =
                            if (value == 0) 50.dp else (value / max.toFloat()) * barHeight
                        Box(
                            modifier = Modifier
                                .padding(vertical = 8.dp, horizontal = 2.dp)
                                .height(boxHeight)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        when {
                                            value == 0 -> Color.Transparent
                                            index == 6 -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.secondary
                                        },
                                        RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                    )
                                    .fillMaxSize()
                            ) {

                            }
                        }
                        Box(
                            modifier = Modifier
                                .padding(vertical = 8.dp, horizontal = 2.dp)
                                .height(if (boxHeight < 20.dp) 50.dp else boxHeight)
                                .background(color = Color.Transparent)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = value.toString(),
                                color = when {
                                    boxHeight < 20.dp -> MaterialTheme.colorScheme.onBackground
                                    value == 0 -> MaterialTheme.colorScheme.onBackground
                                    index == 6 -> MaterialTheme.colorScheme.onPrimary
                                    else -> MaterialTheme.colorScheme.onSecondary
                                },
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
            if (weekDayFrequencies.none { it > 0 }) {
                Text(
                    text = "Looks like you haven't completed any tasks for this pile in a while...",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
                    textAlign = TextAlign.Center
                )
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

@Preview
@Composable
fun FrequencyChartPreviewAllZeros() {
    PileyTheme(useDarkTheme = true) {
        val last7Days = listOf(0, 0, 0, 0, 0, 0, 0)
        FrequencyChart(last7Days, LocalDate.of(2023, 4, 29))
    }
}


@Preview
@Composable
fun FrequencyChartPreviewLargeContrast() {
    PileyTheme(useDarkTheme = true) {
        val last7Days = listOf(0, 0, 0, 20, 0, 0, 1)
        FrequencyChart(last7Days, LocalDate.of(2023, 4, 29))
    }
}

