package com.dk.piley.ui.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.ui.theme.PileyTheme
import com.jakewharton.threetenabp.AndroidThreeTen
import org.threeten.bp.LocalDateTime

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReminderTimeSuggestions(
    modifier: Modifier = Modifier,
    onSelectTimeSuggestion: (LocalDateTime) -> Unit = {}
) {
    val context = LocalContext.current
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val timeSuggestions = TimeSuggestion.values()
        timeSuggestions
            .map { timeSuggestion -> timeSuggestion.getLabelAndDate(context, LocalDateTime.now()) }
            .sortedBy { (_, date) -> date }
            .map { (label, date) ->
                ElevatedSuggestionChip(
                    onClick = { onSelectTimeSuggestion(date) },
                    label = { Text(text = label) }
                )
            }
    }
}

@Preview
@Composable
fun ReminderTimeSuggestionsPreview() {
    AndroidThreeTen.init(LocalContext.current)
    PileyTheme(useDarkTheme = true) {
        ReminderTimeSuggestions()
    }
}