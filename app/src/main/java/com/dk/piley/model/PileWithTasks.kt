package com.dk.piley.model

import androidx.room.Embedded
import androidx.room.Relation
import com.dk.piley.model.task.Task

data class PileWithTasks(
    @Embedded val pile: Pile,
    @Relation(
        parentColumn = "pileId",
        entityColumn = "pileId"
    )
    val tasks: List<Task>
)
