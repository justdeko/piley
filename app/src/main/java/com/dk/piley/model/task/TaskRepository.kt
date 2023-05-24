package com.dk.piley.model.task

import com.dk.piley.reminder.NotificationManager
import com.dk.piley.reminder.ReminderManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val reminderManager: ReminderManager,
    private val notificationManager: NotificationManager,
) {
    fun getTasks(): Flow<List<Task>> = taskDao.getTasks()

    fun getTaskById(taskId: Long): Flow<Task> = taskDao.getTaskById(taskId)

    suspend fun insertTask(task: Task): Long {
        // remove notification or scheduled alarms if task is set to done/deleted
        if (task.status == TaskStatus.DONE || task.status == TaskStatus.DELETED) {
            dismissAlarmAndNotification(task.id)
        }
        return taskDao.insertTask(task)
    }

    suspend fun deleteTask(task: Task): Void {
        dismissAlarmAndNotification(task.id)
        return taskDao.deleteTask(task)
    }

    suspend fun deleteAllCompletedDeletedTasksForPile(pileId: Long): Void =
        taskDao.deleteCompletedDeletedForPile(pileId)

    private fun dismissAlarmAndNotification(taskId: Long) {
        reminderManager.cancelReminder(taskId)
        notificationManager.dismiss(taskId)
    }
}