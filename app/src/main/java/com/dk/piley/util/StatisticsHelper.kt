package com.dk.piley.util

import android.content.Context
import com.dk.piley.R
import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskStatus
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoUnit
import kotlin.math.roundToLong

fun pileFrequenciesForDates(pileWithTasks: PileWithTasks): Map<LocalDate, Int> =
    pileWithTasks.tasks
        .filter {
            it.status == TaskStatus.DONE && LocalDateTime.now().minusWeeks(1)
                .isBefore(it.modifiedAt)
        }
        .groupingBy { it.modifiedAt.toLocalDate() }
        .eachCount()

fun pileFrequenciesForDatesWithZeros(frequencyMap: Map<LocalDate, Int>): Map<LocalDate, Int> {
    val finalMap = mutableMapOf<LocalDate, Int>()
    for (i in 0..6) {
        val date = LocalDateTime.now().minusDays(i.toLong()).toLocalDate()
        val existingValue = frequencyMap[date]
        val mapValue = existingValue ?: 0
        finalMap[date] = mapValue
    }
    return finalMap
}

fun getCompletedTasksForWeekValues(pileWithTasks: PileWithTasks): List<Int> =
    pileFrequenciesForDatesWithZeros(
        pileFrequenciesForDates(pileWithTasks)
    ).values.toList().reversed()


fun getUpcomingTasks(pilesWithTasks: List<PileWithTasks>): List<Pair<String, Task>> =
    pilesWithTasks.flatMap { pileWithTasks ->
        pileWithTasks.tasks
            .filter {
                (it.reminder != null && it.status == TaskStatus.DEFAULT)
                        || (it.reminder != null && it.isRecurring && it.status != TaskStatus.DELETED)
            }
            .map { Pair(pileWithTasks.pile.name, it) }
    }.sortedBy { it.second.reminder }.take(3)

fun getBiggestPileName(pilesWithTasks: List<PileWithTasks>, context: Context): String =
    pilesWithTasks.maxByOrNull { pileWithTasks ->
        pileWithTasks.tasks.count { it.status == TaskStatus.DEFAULT }
    }?.pile?.name ?: context.getString(R.string.no_pile)

fun getAverageTaskCompletionInHours(pilesWithTasks: List<PileWithTasks>): Long {
    val taskDurations = pilesWithTasks.flatMap { pileWithTasks ->
        pileWithTasks.tasks
            .filter { it.status == TaskStatus.DONE }
            .map(Task::getTaskAverageCompletionDurationInHours)
    }
    return if (taskDurations.isNotEmpty()) {
        taskDurations.average().roundToLong()
    } else 0
}

fun Task.getTaskAverageCompletionDurationInHours(): Long {
    return (
            listOf(createdAt.atZone(ZoneId.systemDefault()).toInstant()) + completionTimes)
        .zipWithNext { a, b ->
            ChronoUnit.HOURS.between(a, b)
        }.average().roundToLong()
}