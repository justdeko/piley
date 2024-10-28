package com.dk.piley.ui.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dk.piley.ui.common.LocalDim
import kotlinx.datetime.LocalDateTime

/**
 * Reminder time suggestions list
 *
 * @param modifier generic modifier
 * @param onSelectTimeSuggestion on time suggestion item selected
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReminderTimeSuggestions(
    modifier: Modifier = Modifier,
    onSelectTimeSuggestion: (LocalDateTime) -> Unit = {}
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(LocalDim.current.medium)
    ) {
        val timeSuggestions = TimeSuggestion.entries.toTypedArray()
        timeSuggestions
            .map { timeSuggestion -> timeSuggestion.getLabelAndDate() }
            .sortedBy { (_, date) -> date }
            .map { (label, date) ->
                ElevatedSuggestionChip(
                    onClick = { onSelectTimeSuggestion(date) },
                    label = { Text(text = label) }
                )
            }
    }
}
