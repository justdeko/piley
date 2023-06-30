package com.dk.piley.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringArrayResource
import com.dk.piley.R
import com.dk.piley.model.task.RecurringTimeRange
import com.dk.piley.model.task.RecurringTimeRange.DAILY
import com.dk.piley.model.task.RecurringTimeRange.MONTHLY
import com.dk.piley.model.task.RecurringTimeRange.WEEKLY
import com.dk.piley.model.task.RecurringTimeRange.YEARLY
import org.threeten.bp.LocalDateTime

fun getNextReminderTime(
    lastReminder: LocalDateTime,
    recurringTimeRange: RecurringTimeRange,
    recurringFrequency: Int
): LocalDateTime = when (recurringTimeRange) {
    DAILY -> lastReminder.plusDays(recurringFrequency.toLong())
    WEEKLY -> lastReminder.plusWeeks(recurringFrequency.toLong())
    MONTHLY -> lastReminder.plusMonths(recurringFrequency.toLong())
    YEARLY -> lastReminder.plusYears(recurringFrequency.toLong())
}

@Composable
fun getFrequencyString(
    recurringTimeRange: RecurringTimeRange,
    recurringFrequency: Int
): String {
    val pluralS = pluralStringResource(id = R.plurals.plural_s, count = recurringFrequency)
    val timeRanges = stringArrayResource(id = R.array.time_range)
    val timeRangeString = when (recurringTimeRange) {
        DAILY -> timeRanges[0]
        WEEKLY -> timeRanges[1]
        MONTHLY -> timeRanges[2]
        YEARLY -> timeRanges[3]
    }
    val frequency = if (recurringFrequency == 1) "" else recurringFrequency.toString()
    return "Repeats: Every $frequency ${timeRangeString}$pluralS"
}