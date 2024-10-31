package com.dk.piley.reminder

import com.dk.piley.model.task.Task

interface INotificationManager {
    suspend fun showNotification(task: Task, pileName: String?)
    fun dismiss(taskId: Long)
}
