package com.dk.piley.ui.charts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.ui.theme.PileyTheme
import kotlinx.datetime.LocalDate


@Preview
@Composable
fun FrequencyChartPreview() {
    PileyTheme(useDarkTheme = true) {
        val last7Days = listOf(10, 15, 20, 18, 13, 12, 8)
        FrequencyChart(last7Days, LocalDate(2023, 4, 29))
    }
}

@Preview
@Composable
fun FrequencyChartPreviewAllZeros() {
    PileyTheme(useDarkTheme = true) {
        val last7Days = listOf(0, 0, 0, 0, 0, 0, 0)
        FrequencyChart(last7Days, LocalDate(2023, 4, 29))
    }
}


@Preview
@Composable
fun FrequencyChartPreviewLargeContrast() {
    PileyTheme(useDarkTheme = true) {
        val last7Days = listOf(0, 0, 0, 20, 0, 0, 1)
        FrequencyChart(last7Days, LocalDate(2023, 4, 29))
    }
}

@Preview
@Composable
fun FrequencyChartPreviewLargeContrastSmallWidth() {
    PileyTheme(useDarkTheme = true) {
        val last7Days = listOf(0, 0, 0, 20, 0, 0, 1)
        Column(
            Modifier
                .fillMaxWidth()
                .padding(40.dp)
        ) {
            FrequencyChart(last7Days, LocalDate(2023, 4, 29))
        }
    }
}