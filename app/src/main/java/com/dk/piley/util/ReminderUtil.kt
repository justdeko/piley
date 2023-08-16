package com.dk.piley.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.dk.piley.R
import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.task.RecurringTimeRange
import com.dk.piley.model.task.RecurringTimeRange.DAILY
import com.dk.piley.model.task.RecurringTimeRange.MONTHLY
import com.dk.piley.model.task.RecurringTimeRange.WEEKLY
import com.dk.piley.model.task.RecurringTimeRange.YEARLY
import com.dk.piley.model.task.Task
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

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

fun Task.getNextReminderTime(): Instant =
    getNextReminderTime(
        LocalDateTime.ofInstant(
            Instant.now(),
            ZoneId.systemDefault()
        ), recurringTimeRange, recurringFrequency
    ).toInstantWithOffset()

@Composable
fun getFrequencyString(
    recurringTimeRange: RecurringTimeRange,
    recurringFrequency: Int
): String {
    val pluralS = pluralStringResource(id = R.plurals.plural_s, count = recurringFrequency)
    val frequency = if (recurringFrequency == 1) "" else "$recurringFrequency "
    return stringResource(
        R.string.reminder_repeat_frequency_value,
        "$frequency${recurringTimeRange.toText()}$pluralS"
    )
}

@Composable
fun RecurringTimeRange.toText(): String {
    val timeRanges = stringArrayResource(id = R.array.time_range)
    return when (this) {
        DAILY -> timeRanges[0]
        WEEKLY -> timeRanges[1]
        MONTHLY -> timeRanges[2]
        YEARLY -> timeRanges[3]
    }
}

fun String.toRecurringTimeRange(context: Context): RecurringTimeRange {
    val timeRanges = context.resources.getStringArray(R.array.time_range)
    return when (this) {
        timeRanges[0] -> DAILY
        timeRanges[1] -> WEEKLY
        timeRanges[2] -> MONTHLY
        timeRanges[3] -> YEARLY
        else -> DAILY
    }
}

fun getPileNameForTaskId(taskId: Long, pilesWithTasks: List<PileWithTasks>) =
    pilesWithTasks.flatMap { pileWithTasks ->
        pileWithTasks.tasks.map {
            Pair(
                pileWithTasks.pile.name,
                it.id
            )
        }
    }.find { (_, id) -> id == taskId }?.first