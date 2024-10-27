package com.dk.piley.reminder

import kotlinx.datetime.Instant

interface IReminderManager {
    fun startReminder(reminderTime: Instant, taskId: Long)
    fun cancelReminder(taskId: Long)
}
