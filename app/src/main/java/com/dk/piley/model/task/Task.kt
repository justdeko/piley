package com.dk.piley.model.task

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "",
    val pileId: Long = 0,
    val description: String = "",
    val createdAt: Long = 0,
    val modifiedAt: Long = 0,
    var status: TaskStatus = TaskStatus.DEFAULT,
)
