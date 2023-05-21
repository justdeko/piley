package com.dk.piley.util

import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskStatus
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

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
            .filter { it.reminder != null }
            .map { Pair(pileWithTasks.pile.name, it) }
    }.sortedBy { it.second.reminder }.take(3)