package com.dk.piley.reminder

import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskRepository
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.model.user.UserRepository
import com.dk.piley.util.getPileNameForTaskId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import javax.inject.Inject

class ReminderActionHandler @Inject constructor(
    private val reminderManager: ReminderManager,
    private val notificationManager: NotificationManager,
    private val taskRepository: TaskRepository,
    private val pileRepository: PileRepository,
    private val userRepository: UserRepository,
) : IReminderActionHandler {
    override fun show(taskId: Long): Flow<Task> {
        return taskRepository.getTaskById(taskId).take(1)
            .filter { it.reminder != null && it.status != TaskStatus.DELETED }
            .onEach { task ->
                val pileName =
                    getPileNameForTaskId(taskId, pileRepository.getPilesWithTasks().first())
                notificationManager.showNotification(task, pileName)
                // set status to default if task is recurring so the task shows up again
                if (task.isRecurring && task.status == TaskStatus.DONE) {
                    taskRepository.insertTask(
                        task.copy(status = TaskStatus.DEFAULT)
                    )
                }
            }
    }

    override fun restartAll(): Flow<List<Task>> {
        return taskRepository.getTasks().take(1).onEach { taskList ->
            taskList.filter {
                // only tasks that are either recurring or not completed yet, and reminder in the future
                ((it.status == TaskStatus.DEFAULT && it.reminder != null)
                        || (it.status != TaskStatus.DELETED && it.reminder != null && it.isRecurring)
                        ) && it.reminder.isAfter(Instant.now())
            }.forEach { task ->
                // start a reminder if task recurring or not done yet
                task.reminder?.let { reminder ->
                    if (task.status == TaskStatus.DEFAULT
                        || (task.status == TaskStatus.DONE && task.isRecurring)
                    ) {
                        reminderManager.startReminder(reminder, task.id)
                    }
                }
            }
        }
    }

    override suspend fun complete(taskId: Long): Flow<Task> {
        return taskRepository.getTaskById(taskId).take(1).onEach {
            // set task to done
            // cancelling notification and setting next reminder is handled in repository
            taskRepository.insertTask(
                it.copy(status = TaskStatus.DONE)
            )
        }
    }

    override fun delay(taskId: Long): Flow<Task> {
        // no task found
        if (taskId.toInt() == -1) return emptyFlow()
        return taskRepository.getTaskById(taskId).onEach { task ->
            userRepository.getSignedInUser().first()?.let { user ->
                // delay by n minutes
                val newReminderTime =
                    Instant.now().plus(Duration.ofMinutes(user.defaultReminderDelay.toLong()))
                // update reminder time in db
                taskRepository.insertTask(task.copy(reminder = newReminderTime))
                // start new reminder
                reminderManager.startReminder(newReminderTime, task.id)
                notificationManager.dismiss(taskId)
            }
        }
    }
}
