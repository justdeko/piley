package com.dk.piley.reminder

import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskDao
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.model.user.UserRepository
import com.dk.piley.util.getNextReminderTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
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
            .filter { it.reminder != null && it.status != TaskStatus.DELETED }
            .onEach {
                notificationManager.showNotification(it)
                // set status to default if task is recurring so the task shows up again
                if (it.isRecurring && it.status == TaskStatus.DONE) {
                    taskDao.insertTask(it.copy(status = TaskStatus.DEFAULT))
                }
            }
    }

    override fun restartAll(): Flow<List<Task>> {
        return taskDao.getTasks().take(1).onEach { taskList ->
            taskList.take(1).filter {
                // only tasks that are either recurring or not completed yet
                (it.status == TaskStatus.DEFAULT && it.reminder != null)
                    || (it.status != TaskStatus.DELETED && it.reminder != null && it.isRecurring)
            }.forEach { task ->
                // start a reminder
                task.reminder?.let { reminder ->
                    reminderManager.startReminder(reminder, task.id)
                } // TODO make recurring reminder with offset
            }
        }
    }

    override suspend fun complete(taskId: Long): Flow<Task> {
        return taskDao.getTaskById(taskId).take(1).onEach {
            reminderManager.cancelReminder(taskId)
            // set next reminder if task is recurring
            if (it.isRecurring && it.reminder != null) {
                reminderManager.startReminder(
                    reminderDateTime = getNextReminderTime(
                        it.reminder,
                        it.recurringTimeRange,
                        it.recurringFrequency
                    ),
                    taskId = taskId
                )
            }
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
