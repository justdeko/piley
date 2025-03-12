package com.dk.piley.reminder

import com.dk.piley.model.task.Task
import kotlinx.datetime.Instant

class ReminderManager : IReminderManager {
    override suspend fun startReminder(
        reminderTime: Instant,
        task: Task,
        actionTitles: Triple<String, String, String>
    ) {
        // do nothing
    }

    override fun cancelReminder(taskId: Long) {
        // do nothing
    }
}