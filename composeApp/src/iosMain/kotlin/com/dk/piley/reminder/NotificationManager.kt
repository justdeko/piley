package com.dk.piley.reminder

import com.dk.piley.model.task.Task

class NotificationManager: INotificationManager {
    override suspend fun showNotification(task: Task, pileName: String?) {
        // do nothing
    }

    override fun dismiss(taskId: Long) {
        // do nothing
    }
}