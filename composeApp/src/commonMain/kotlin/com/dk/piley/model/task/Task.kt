package com.dk.piley.model.task

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.dk.piley.model.pile.Pile
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Task entity representing a task created by the user
 *
 * @property id task id (autogenerated)
 * @property title name of the task
 * @property pileId parent pile id of the task
 * @property description task description
 * @property createdAt timestamp representing when the task was created
 * @property modifiedAt timestamp representing when the task was last modified
 * @property completionTimes list of timestamps when the task was set to "done"
 * @property reminder timestamp of the task reminder
 * @property isRecurring boolean showing whether the task is recurring
 * @property recurringTimeRange time range for the recurring task
 * @property recurringFrequency frequency for the given time range (e.g. every 2 weeks for time range weekly)
 * @property averageCompletionTimeInHours the average completion time of this task in hours
 * @property status task completion status
 */
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Pile::class,
            parentColumns = arrayOf("pileId"),
            childColumns = arrayOf("pileId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "",
    val pileId: Long = 1,
    val description: String = "",
    val createdAt: Instant = Clock.System.now(),
    val modifiedAt: Instant = Clock.System.now(),
    val completionTimes: List<Instant> = emptyList(),
    val reminder: Instant? = null,
    val isRecurring: Boolean = false,
    val recurringTimeRange: RecurringTimeRange = RecurringTimeRange.DAILY,
    val recurringFrequency: Int = 1,
    @ColumnInfo(defaultValue = "0")
    val nowAsReminderTime: Boolean = false,
    val status: TaskStatus = TaskStatus.DEFAULT,
    @ColumnInfo(defaultValue = "0")
    val averageCompletionTimeInHours: Long = 0,
)
