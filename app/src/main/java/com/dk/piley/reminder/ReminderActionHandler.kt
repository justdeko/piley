package com.dk.piley.reminder

import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskDao
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.model.user.UserRepository
import kotlinx.coroutines.flow.*
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

class ReminderActionHandler @Inject constructor(
    private val reminderManager: ReminderManager,
    private val notificationManager: NotificationManager,
    private val taskDao: TaskDao,
    private val userRepository: UserRepository,
) : IReminderActionHandler {
    override fun show(taskId: Long): Flow<Task> {
        return taskDao.getTaskById(taskId).take(1)
            .filter { it.status == TaskStatus.DEFAULT && it.reminder != null }.onEach {
                notificationManager.showNotification(it)
            }
    }

    override fun restartAll(): Flow<List<Task>> {
        return taskDao.getTasks().take(1).onEach { taskList ->
            taskList.take(1).filter { it.status == TaskStatus.DEFAULT && it.reminder != null }
                .forEach { task ->
                    task.reminder?.let { reminder ->
                        reminderManager.startReminder(reminder, task.id)
                    }
                }
        }
    }

    override suspend fun complete(taskId: Long): Flow<Task> {
        return taskDao.getTaskById(taskId).take(1).onEach {
            reminderManager.cancelReminder(taskId)
            notificationManager.dismiss(taskId)
            taskDao.insertTask(it.copy(status = TaskStatus.DONE))
        }
    }

    override fun delay(taskId: Long): Flow<Task> {
        // no task found
        if (taskId.toInt() == -1) return emptyFlow()
        return taskDao.getTaskById(taskId).onEach { task ->
            userRepository.getSignedInUser().first()?.let { user ->
                reminderManager.startReminder(
                    LocalDateTime.now().plusMinutes(user.defaultReminderDelay.toLong()), task.id
                )
                notificationManager.dismiss(taskId)
            }
        }
    }
}
