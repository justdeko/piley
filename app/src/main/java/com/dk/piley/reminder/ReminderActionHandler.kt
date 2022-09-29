package com.dk.piley.reminder

import android.util.Log
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskDao
import com.dk.piley.model.task.TaskStatus
import kotlinx.coroutines.flow.*
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

class ReminderActionHandler @Inject constructor(
    private val reminderManager: ReminderManager,
    private val notificationManager: NotificationManager,
    private val taskDao: TaskDao,
) : IReminderActionHandler {
    override fun show(taskId: Long): Flow<Task> {
        return taskDao.getTaskById(taskId)
            .filter { it.status == TaskStatus.DEFAULT && it.reminder != null }
            .onEach { notificationManager.showNotification(it) }
    }

    override fun restartAll(): Flow<List<Task>> {
        return taskDao.getTasks().take(1).onEach { taskList ->
            taskList.filter { it.status == TaskStatus.DEFAULT && it.reminder != null }
                .forEach { task ->
                    task.reminder?.let { reminder ->
                        reminderManager.startReminder(reminder, task.id)
                    }
                }
        }
    }

    override suspend fun complete(taskId: Long): Flow<Task> {
        // no task found
        if (taskId.toInt() == -1) return emptyFlow()
        Log.d("ReminderActionHandler", "Completing task with id $taskId")
        return taskDao.getTaskById(taskId).onEach {
            taskDao.insertTask(it.apply {
                this.status = TaskStatus.DONE
            })
            reminderManager.cancelReminder(taskId)
            notificationManager.dismiss(taskId)
        }
    }

    override fun delay(taskId: Long): Flow<Task> {
        // no task found
        if (taskId.toInt() == -1) return emptyFlow()
        return taskDao.getTaskById(taskId).onEach {
            reminderManager.startReminder(LocalDateTime.now().plusMinutes(30), it.id)
            notificationManager.dismiss(taskId)
        }
    }
}
