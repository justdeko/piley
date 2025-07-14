package com.dk.piley.util

import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskStatus
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

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
 * Get completed task counts for week values
 *
 * @param tasks list of tasks to calculate the completion counts for
 * @return a list of completion frequencies for each of the past 7 days in reverse (starting with today)
 */
fun getCompletedTasksForWeekValues(
    tasks: List<Task>
): List<Int> =
    pileFrequenciesForDatesWithZerosForLast7Days(
        pileFrequenciesForDates(PileWithTasks(Pile(), tasks))
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
fun getBiggestPileName(pilesWithTasks: List<PileWithTasks>): String? =
    pilesWithTasks.maxByOrNull { pileWithTasks ->
        pileWithTasks.tasks.count { it.status == TaskStatus.DEFAULT }
    }?.pile?.name

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
 * Sort [PileWithTasks] list by the order of the pile ids in the given list
 * If the pile id is not found in the list, it will be sorted by the pile id
 *
 * @param pileOrder list of pile ids to use as the order
 * @return sorted list of piles with tasks
 */
fun List<PileWithTasks>.sortedWithOrder(
    pileOrder: List<Long>,
): List<PileWithTasks> = sortedBy {
    val indexInOrder = pileOrder.indexOf(it.pile.pileId)
    // attempt to sort by pile order, if not found, use pile id
    // add pile order size to pile id to ensure new piles are at the end
    if (indexInOrder == -1) it.pile.pileId.toInt() + pileOrder.size else indexInOrder
}