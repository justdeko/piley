package com.dk.piley.ui.task

import androidx.compose.runtime.Composable
import com.dk.piley.util.timeString
import com.dk.piley.util.toLocalDateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.time_of_day
import piley.composeapp.generated.resources.time_suggestion
import piley.composeapp.generated.resources.tomorrow_prefix

/**
 * Time suggestion enum representing the suggested reminder value,
 * for example (tomorrow) morning, afternoon, etc.
 *
 */
enum class TimeSuggestion {
    MORNING, AFTERNOON, EVENING
}

/**
 * Get label and date given the time suggestion enum value
 * Calculated using current time so no times in the past are shown,
 * for example today morning despite it being afternoon
 *
 * @param currentDateTime current date time
 * @return pair of the suggested time string (e.g. "Tomorrow Afternoon (13:00)") and its corresponding [LocalDateTime] value
 */
@Composable
fun TimeSuggestion.getLabelAndDate(
    currentDateTime: LocalDateTime = Clock.System.now().toLocalDateTime()
): Pair<String, LocalDateTime> {
    val timeOfDayNames = stringArrayResource(Res.array.time_of_day)
    val timeOfDayString = when (this) {
        TimeSuggestion.MORNING -> timeOfDayNames[0]
        TimeSuggestion.AFTERNOON -> timeOfDayNames[1]
        TimeSuggestion.EVENING -> timeOfDayNames[2]
    }
    val timeOfDay = when (this) {
        TimeSuggestion.MORNING -> LocalTime(8, 0)
        TimeSuggestion.AFTERNOON -> LocalTime(13, 0)
        TimeSuggestion.EVENING -> LocalTime(20, 0)
    }
    val isTomorrow = currentDateTime.time >= timeOfDay
    val tomorrowPrefix = if (isTomorrow) {
        stringResource(Res.string.tomorrow_prefix)
    } else ""
    val now = Clock.System.now()
    val dateTime = if (isTomorrow) now.plus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
        .toLocalDateTime() else now.toLocalDateTime()
    return Pair(
        stringResource(
            Res.string.time_suggestion,
            tomorrowPrefix,
            timeOfDayString,
            timeOfDay.timeString()
        ), LocalDateTime(dateTime.date, timeOfDay)
    )
}