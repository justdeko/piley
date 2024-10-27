package com.dk.piley.reminder

import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskRepository
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.model.user.UserRepository
import com.dk.piley.util.calculateDelayDuration
import com.dk.piley.util.getPileNameForTaskId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take

/**
 * Reminder action handler that performs the necessary operations given the specific action
 *
 * @property reminderManager instance of reminder manager to set and dismiss alarms
 * @property notificationManager instance of notification manager to set and dismiss notifications
 * @property taskRepository instance of task repository to perform db operations regarding tasks
 * @property pileRepository instance of pile repository to perform db operations regarding piles
 * @property userRepository instance of user repository to perform db operations regarding user
 */
class ReminderActionHandler(
    private val reminderManager: IReminderManager,
    private val notificationManager: INotificationManager,
    private val taskRepository: TaskRepository,
    private val pileRepository: PileRepository,
    private val userRepository: UserRepository,
) : IReminderActionHandler {

    /**
     * Show the notification for a specific task id
     *
     * @param taskId task id to show the notification for
     * @return flow representing the show operation
     */
    override fun show(taskId: Long): Flow<Task?> {
        return taskRepository.getTaskById(taskId).take(1)
            .onEach { task ->
                // don't show notification if already deleted or no reminder anymore
                if (task?.reminder == null || task.status == TaskStatus.DELETED) return@onEach
                val pileName =
                    getPileNameForTaskId(taskId, pileRepository.getPilesWithTasks().first())
                notificationManager.showNotification(task, pileName)
            }
    }

    /**
     * Restart all task reminders
     *
     * @return flow representing the restart operation
     */
    override fun restartAll() = taskRepository.restartAlarms()

    /**
     * Complete the task for a given id
     *
     * @param taskId task id to perform the completion for
     * @return
     */
    override suspend fun complete(taskId: Long): Flow<Task?> {
        return taskRepository.getTaskById(taskId).take(1).onEach {
            // task already deleted
            if (it == null) {
                notificationManager.dismiss(taskId)
                return@onEach
            }
            // set task to done
            // cancelling notification and setting next reminder is handled in repository
            taskRepository.insertTaskWithStatus(
                it.copy(status = TaskStatus.DONE)
            )
        }
    }

    /**
     * Delay reminder of a specific task
     *
     * @param taskId task id of the task to perform the delay for
     * @return flow representing the delay operation
     */
    override suspend fun delay(taskId: Long): Flow<Task?> {
        // no task found
        if (taskId.toInt() == -1) return emptyFlow()
        return taskRepository.getTaskById(taskId).take(1).onEach { task ->
            // task already deleted
            if (task == null) {
                notificationManager.dismiss(taskId)
                return@onEach
            }
            userRepository.getSignedInUser().first()?.let { user ->
                val delayInMinutes = calculateDelayDuration(
                    delayRange = user.defaultReminderDelayRange,
                    delayDurationIndex = user.defaultReminderDelayIndex
                )
                taskRepository.delayTask(task, delayInMinutes)
            }
        }
    }

    override suspend fun customDelay(taskId: Long): Flow<Task?> {
        // no task found
        if (taskId.toInt() == -1) return emptyFlow()
        // for now no action
        return taskRepository.getTaskById(taskId).take(1)
    }
}
