package com.dk.piley.util

import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.task.TaskStatus
import org.threeten.bp.LocalDateTime

fun getCompletedTasksForWeekValues(pileWithTasks: PileWithTasks): List<Float> = pileWithTasks.tasks
    .filter {
        it.status == TaskStatus.DONE && LocalDateTime.now().minusWeeks(1).isBefore(it.modifiedAt)
    }
    .groupingBy { it.modifiedAt.toLocalDate() }
    .eachCount()
    .values.toList().map { it.toFloat() }
