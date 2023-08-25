package com.dk.piley.ui.task

import android.content.Context
import com.dk.piley.R
import com.dk.piley.util.timeString
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

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
 * @param context generic context
 * @param currentDateTime current date time
 * @return pair of the suggested time string (e.g. "Tomorrow Afternoon (13:00)") and its corresponding [LocalDateTime] value
 */
fun TimeSuggestion.getLabelAndDate(
    context: Context,
    currentDateTime: LocalDateTime
): Pair<String, LocalDateTime> {
    val timeOfDayNames = context.resources.getStringArray(R.array.time_of_day)
    val timeOfDayString = when (this) {
        TimeSuggestion.MORNING -> timeOfDayNames[0]
        TimeSuggestion.AFTERNOON -> timeOfDayNames[1]
        TimeSuggestion.EVENING -> timeOfDayNames[2]
    }
    val timeOfDay = when (this) {
        TimeSuggestion.MORNING -> LocalTime.of(8, 0)
        TimeSuggestion.AFTERNOON -> LocalTime.of(13, 0)
        TimeSuggestion.EVENING -> LocalTime.of(20, 0)
    }
    val isTomorrow = currentDateTime.toLocalTime().isAfter(timeOfDay)
    val tomorrowPrefix = if (isTomorrow) {
        "${context.getString(R.string.tomorrow_prefix)} "
    } else ""
    val date = if (isTomorrow) LocalDate.now().plusDays(1) else LocalDate.now()
    val dateTime = LocalDateTime.of(date, timeOfDay)
    return Pair(
        context.getString(
            R.string.time_suggestion,
            tomorrowPrefix,
            timeOfDayString,
            timeOfDay.timeString()
        ), dateTime
    )
}