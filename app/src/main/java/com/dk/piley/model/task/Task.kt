package com.dk.piley.model.task

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dk.piley.util.utcZoneId
import org.threeten.bp.LocalDateTime

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "",
    val pileId: Long = 1,
    val description: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(utcZoneId),
    val modifiedAt: LocalDateTime = LocalDateTime.now(utcZoneId),
    val reminder: LocalDateTime? = null,
    val isRecurring: Boolean = false,
    val recurringTimeRange: RecurringTimeRange = RecurringTimeRange.DAILY,
    val recurringFrequency: Int = 1,
    val status: TaskStatus = TaskStatus.DEFAULT,
)
