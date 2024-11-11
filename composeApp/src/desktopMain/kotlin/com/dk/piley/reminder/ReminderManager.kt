package com.dk.piley.reminder

import com.dk.piley.model.task.Task
import kotlinx.datetime.Instant

class ReminderManager: IReminderManager {
    override suspend fun startReminder(reminderTime: Instant, task: Task) {
        // TODO("Not yet implemented")
    }

    override fun cancelReminder(taskId: Long) {
        // TODO("Not yet implemented")
    }
}