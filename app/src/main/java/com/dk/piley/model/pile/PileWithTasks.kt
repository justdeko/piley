package com.dk.piley.model.pile

import androidx.room.Embedded
import androidx.room.Relation
import com.dk.piley.model.task.Task

/**
 * Represents a 1-to-n relation of a pile and its tasks
 *
 * @property pile the pile
 * @property tasks the tasks of the given pile
 */
data class PileWithTasks(
    @Embedded val pile: Pile,
    @Relation(
        parentColumn = "pileId",
        entityColumn = "pileId"
    )
    val tasks: List<Task>
)
