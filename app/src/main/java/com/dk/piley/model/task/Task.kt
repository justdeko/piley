package com.dk.piley.model.task

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var title: String = "",
    val pileId: Long = 0,
    var description: String = "",
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var reminder: Date? = null,
    var status: TaskStatus = TaskStatus.DEFAULT,
)
