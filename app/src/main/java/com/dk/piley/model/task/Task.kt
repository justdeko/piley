package com.dk.piley.model.task

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dk.piley.ui.util.utcZoneId
import org.threeten.bp.LocalDateTime

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var title: String = "",
    val pileId: Long = 1,
    var description: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(utcZoneId),
    var modifiedAt: LocalDateTime = LocalDateTime.now(utcZoneId),
    var reminder: LocalDateTime? = null,
    var status: TaskStatus = TaskStatus.DEFAULT,
)
