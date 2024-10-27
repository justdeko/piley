package com.dk.piley.reminder

import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.task.RecurringTimeRange
import com.dk.piley.model.task.RecurringTimeRange.DAILY
import com.dk.piley.model.task.RecurringTimeRange.MONTHLY
import com.dk.piley.model.task.RecurringTimeRange.WEEKLY
import com.dk.piley.model.task.RecurringTimeRange.YEARLY
import com.dk.piley.model.task.Task
import com.dk.piley.util.toInstantWithOffset
import com.dk.piley.util.toLocalDateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant

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

/**
 * Return the delay duration in minutes based on a delay range (minutes, hours, etc.)
 * and a duration
 *
 * @param delayRange the delay range of type [DelayRange]
 * @param delayDurationIndex the delay duration index representing a duration in the map
 * @return delay time in minutes
 */
fun calculateDelayDuration(delayRange: DelayRange, delayDurationIndex: Int): Long {
    val duration = delayRange.getDurationByIndex(delayDurationIndex)
    val factor = when (delayRange) {
        DelayRange.Minute -> 1
        DelayRange.Hour -> 60
        DelayRange.Day -> 1440
        DelayRange.Week -> 10080
        DelayRange.Month -> 43830
    }.toLong()
    return factor * duration
}


/**
 * Get next reminder time for a given task based on reminder settings
 *
 * @return next reminder time in [Instant] form
 */
fun Task.getNextReminderTime(
    now: Instant = Clock.System.now()
): Instant {
    val startingTime = if (nowAsReminderTime) now else reminder ?: now
    var reminderTime = reminderPlusTime(
        startingTime.toLocalDateTime(), recurringTimeRange, recurringFrequency
    ).toInstantWithOffset()
    // recalculate if next reminder time is in the past
    while (reminderTime < now) {
        reminderTime = reminderPlusTime(
            reminderTime.toLocalDateTime(),
            recurringTimeRange,
            recurringFrequency
        ).toInstantWithOffset()
    }
    return reminderTime
}

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
): LocalDateTime {
    val timeZone = TimeZone.currentSystemDefault()
    val instant = lastReminder.toInstant(TimeZone.currentSystemDefault())

    return when (recurringTimeRange) {
        DAILY -> instant.plus(recurringFrequency, DateTimeUnit.DAY, timeZone)
        WEEKLY -> instant.plus(recurringFrequency, DateTimeUnit.WEEK, timeZone)
        MONTHLY -> instant.plus(recurringFrequency, DateTimeUnit.MONTH, timeZone)
        YEARLY -> instant.plus(recurringFrequency, DateTimeUnit.YEAR, timeZone)
    }.toLocalDateTime(timeZone)
}

/**
 * Generates a copy of the given task with the new completion time and newly calculated
 * average completion time added to the copy.
 *
 * @param now generic [Instant.now] provider
 * @return copy of the new task
 */
fun Task.withNewCompletionTime(now: Instant = Clock.System.now()): Task {
    val timeZone = TimeZone.currentSystemDefault()
    val comparisonTime = reminder ?: createdAt
    val completionTimeHours = comparisonTime.periodUntil(now, timeZone).hours
    val newCompletionTime =
        averageCompletionTimeInHours + (completionTimeHours - averageCompletionTimeInHours) / (completionTimes.size + 1)
    return copy(
        completionTimes = completionTimes + now,
        averageCompletionTimeInHours = if (newCompletionTime < 0) 0 else newCompletionTime
    )
}