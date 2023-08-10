package com.dk.piley.ui.task

import android.content.Context
import com.dk.piley.R
import com.dk.piley.util.timeString
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime

enum class TimeSuggestion {
    MORNING, AFTERNOON, EVENING
}

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