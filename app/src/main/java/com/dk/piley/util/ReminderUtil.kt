package com.dk.piley.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.dk.piley.R
import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.task.RecurringTimeRange
import com.dk.piley.model.task.RecurringTimeRange.DAILY
import com.dk.piley.model.task.RecurringTimeRange.MONTHLY
import com.dk.piley.model.task.RecurringTimeRange.WEEKLY
import com.dk.piley.model.task.RecurringTimeRange.YEARLY
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.toText
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Get next reminder time for a specific reminder
 *
 * @param lastReminder last reminder date time
 * @param recurringTimeRange recurring reminder time range to calculate next reminder with
 * @param recurringFrequency recurring reminder frequency to calculate next reminder with
 * @return next reminder date time in [LocalDateTime] form
 */
fun reminderPlusTime(
    lastReminder: LocalDateTime,
    recurringTimeRange: RecurringTimeRange,
    recurringFrequency: Int
): LocalDateTime = when (recurringTimeRange) {
    DAILY -> lastReminder.plusDays(recurringFrequency.toLong())
    WEEKLY -> lastReminder.plusWeeks(recurringFrequency.toLong())
    MONTHLY -> lastReminder.plusMonths(recurringFrequency.toLong())
    YEARLY -> lastReminder.plusYears(recurringFrequency.toLong())
}

/**
 * Get next reminder time for a given task based on reminder settings
 *
 * @return next reminder time in [Instant] form
 */
fun Task.getNextReminderTime(
    now: Instant = LocalDateTime.now().toInstantWithOffset()
): Instant {
    val startingTime = if (nowAsReminderTime) now else reminder ?: now
    var reminderTime = reminderPlusTime(
        LocalDateTime.ofInstant(
            startingTime,
            ZoneId.systemDefault()
        ), recurringTimeRange, recurringFrequency
    ).toInstantWithOffset()
    // recalculate if next reminder time is in the past
    while (reminderTime.isBefore(now)) {
        reminderTime = reminderPlusTime(
            reminderTime.toLocalDateTime(),
            recurringTimeRange,
            recurringFrequency
        ).toInstantWithOffset()
    }
    return reminderTime
}

/**
 * Get frequency string given a recurring reminder time range and frequency
 *
 * @param recurringTimeRange recurring reminder time range
 * @param recurringFrequency recurring reminder frequency
 * @return string representing frequency and time range.
 * E.g. for WEEKLY time range and frequency 2: "Every 2 Weeks"
 */
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

/**
 * Get pile name for a specific task id given a list of piles with tasks
 *
 * @param taskId the task id to find the parent pile name for
 * @param pilesWithTasks list of piles with tasks
 * @return the pile name string or null if no pile found
 */
fun getPileNameForTaskId(taskId: Long, pilesWithTasks: List<PileWithTasks>): String? =
    pilesWithTasks.flatMap { pileWithTasks ->
        pileWithTasks.tasks.map {
            Pair(
                pileWithTasks.pile.name,
                it.id
            )
        }
    }.find { (_, id) -> id == taskId }?.first