package com.dk.piley.reminder

import com.dk.piley.model.task.Task
import kotlinx.datetime.Instant

interface IReminderManager {
    suspend fun startReminder(
        reminderTime: Instant,
        task: Task,
        actionTitles: Triple<String, String, String> = Triple("", "", "")
    )

    fun cancelReminder(taskId: Long)
}
