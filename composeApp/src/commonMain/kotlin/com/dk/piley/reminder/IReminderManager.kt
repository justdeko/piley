package com.dk.piley.reminder

import com.dk.piley.model.task.Task
import kotlin.time.Instant

interface IReminderManager {
    suspend fun startReminder(
        reminderTime: Instant,
        task: Task,
        actionTitles: Triple<String, String, String> = Triple("", "", "")
    )

    fun cancelReminder(taskId: Long)
}
