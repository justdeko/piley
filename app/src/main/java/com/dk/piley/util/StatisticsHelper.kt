package com.dk.piley.util

import android.content.Context
import com.dk.piley.R
import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskStatus
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.periodUntil
import kotlinx.datetime.toLocalDateTime

/**
 * Get frequencies of completed tasks for dates
 *
 * @param pileWithTasks pile with tasks to get the frequencies for
 * @return map of date keys and completed task frequency values
 */
fun pileFrequenciesForDates(
    pileWithTasks: PileWithTasks,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): Map<LocalDate, Int> =
    pileWithTasks.tasks
        .filter {
            it.status == TaskStatus.DONE && (Clock.System.now()
                .minus(1, DateTimeUnit.WEEK, timeZone) < it.modifiedAt)
        } // only include completed tasks from the past week
        .map { it.completionTimes } // mop to lists of completion times
        .flatten() // flatten lists into a single list of completion times
        .groupingBy { it.toLocalDateTime(timeZone).date } // group by completion date
        .eachCount()

/**
 * Get completed task frequencies for the last 7 days
 *
 * @param frequencyMap calculated frequency map of completed tasks using [pileFrequenciesForDates]
 * @return map of date keys within the last 7 days and completed task counts as values
 */
fun pileFrequenciesForDatesWithZerosForLast7Days(
    frequencyMap: Map<LocalDate, Int>,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): Map<LocalDate, Int> {
    val finalMap = mutableMapOf<LocalDate, Int>()
    for (i in 0..6) {
        val date =
            Clock.System.now().minus(i, DateTimeUnit.DAY, timeZone).toLocalDateTime(timeZone).date
        val existingValue = frequencyMap[date]
        val mapValue = existingValue ?: 0
        finalMap[date] = mapValue
    }
    return finalMap
}

/**
 * Get list of completed task counts for the past 7 days
 *
 * @param pileWithTasks pile with tasks to calculate the completion counts for
 * @return a list of completion frequencies for each of the past 7 days in reverse (starting with today)
 */
fun getCompletedTasksForWeekValues(pileWithTasks: PileWithTasks): List<Int> =
    pileFrequenciesForDatesWithZerosForLast7Days(
        pileFrequenciesForDates(pileWithTasks)
    ).values.toList().reversed()


/**
 * Get list of 10 most upcoming/urgent tasks (nearest reminder times)
 *
 * @param pilesWithTasks list of piles with tasks to get the tasks from
 * @return list of pairs containing the pile name and the task
 */
fun getUpcomingTasks(pilesWithTasks: List<PileWithTasks>): List<Pair<String, Task>> =
    pilesWithTasks.flatMap { pileWithTasks ->
        pileWithTasks.tasks
            .filter {
                (it.reminder != null && it.status == TaskStatus.DEFAULT)
                        || (it.reminder != null && it.isRecurring && it.status != TaskStatus.DELETED)
            }
            .map { Pair(pileWithTasks.pile.name, it) }
    }.sortedBy { it.second.reminder }.take(10)

/**
 * Get the name of the pile with the most uncompleted tasks
 *
 * @param pilesWithTasks list of piles with tasks to find the biggest pile in
 * @param context generic context to get string resources
 * @return name of the biggest pile or "None" if no pile found or list is empty
 */
fun getBiggestPileName(pilesWithTasks: List<PileWithTasks>, context: Context): String =
    pilesWithTasks.maxByOrNull { pileWithTasks ->
        pileWithTasks.tasks.count { it.status == TaskStatus.DEFAULT }
    }?.pile?.name ?: context.getString(R.string.no_pile)

/**
 * Get tasks completed in past n days
 *
 * @param tasks list of tasks to count the completed tasks for
 * @param days number of days to count the completed tasks for
 * @return number of completed tasks in the past n days
 */
fun getTasksCompletedInPastDays(
    tasks: List<Task>, days: Long = 7,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): Int = tasks.count {
    it.status == TaskStatus.DONE
            && (Clock.System.now().minus(days, DateTimeUnit.DAY, timeZone)
            < it.completionTimes.last()
            )
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