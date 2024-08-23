package com.dk.piley.model.task

import com.dk.piley.reminder.NotificationManager
import com.dk.piley.reminder.ReminderManager
import com.dk.piley.util.dateTimeString
import com.dk.piley.util.getNextReminderTime
import com.dk.piley.util.toLocalDateTime
import com.dk.piley.util.withNewCompletionTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import timber.log.Timber
import java.time.Instant
import javax.inject.Inject

/**
 * Task repository for performing database operations regarding tasks
 *
 * @property taskDao the task dao providing an interface to the database
 * @property reminderManager entity of the reminder manager to set and cancel reminders
 * @property notificationManager entity of the notification manager to set and cancel notifications
 */
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val reminderManager: ReminderManager,
    private val notificationManager: NotificationManager,
) {
    private fun getTasks(): Flow<List<Task>> = taskDao.getTasks()

    fun getTaskById(taskId: Long): Flow<Task?> = taskDao.getTaskById(taskId)

    /**
     * Insert task and perform additional actions based on task status
     *
     * @param task the task entity
     * @param undo flag signifying whether the action is an undo
     * @return long representing db operation success
     */
    suspend fun insertTaskWithStatus(task: Task, undo: Boolean = false): Long {
        val now = Instant.now()
        // update modification time
        var tempTask = if (undo) task else task.copy(modifiedAt = now)
        // add new completion time
        if (task.status == TaskStatus.DONE && !undo) {
            tempTask = tempTask.withNewCompletionTime(Instant.now())
        }
        // recreate reminder if undo occurred
        if (task.status == TaskStatus.DEFAULT && undo && task.reminder != null) {
            reminderManager.startReminder(
                reminderTime = task.reminder,
                taskId = task.id
            )
        }
        // remove notification or scheduled alarms if task is set to done/deleted
        return if (task.status == TaskStatus.DONE || task.status == TaskStatus.DELETED) {
            dismissAlarmAndNotificationAndInsert(tempTask)
        } else taskDao.insertTask(tempTask)
    }

    /**
     * Insert the task entry
     *
     * @param task the task entity
     * @return long representing db operation success
     */
    suspend fun insertTask(task: Task): Long =
        taskDao.insertTask(task.copy(modifiedAt = Instant.now()))

    suspend fun deleteAllCompletedDeletedTasksForPile(pileId: Long): Void =
        taskDao.deleteCompletedDeletedForPile(pileId)

    /**
     * Dismiss alarm and notifications for a given task and create new ones if it is recurring
     * also insert the modified task
     *
     * @param task the task entity to dismiss and recreate reminders for and insert
     * @return long representing db operation success
     */
    private suspend fun dismissAlarmAndNotificationAndInsert(task: Task): Long {
        Timber.d("dismiss and start reminder from repository")
        reminderManager.cancelReminder(task.id)
        notificationManager.dismiss(task.id)
        // set next reminder if task is recurring
        if (task.status != TaskStatus.DELETED && task.isRecurring && task.reminder != null) {
            task.getNextReminderTime().let {
                Timber.d(
                    "set next reminder time for recurring reminder: ${
                        it.toLocalDateTime().dateTimeString()
                    }"
                )
                reminderManager.startReminder(
                    reminderTime = it,
                    taskId = task.id
                )
                // set new reminder time inside task
                return taskDao.insertTask(
                    task.copy(
                        reminder = it,
                    )
                )
            }
        }
        // clear reminder value
        return taskDao.insertTask(task.copy(reminder = null))
    }

    /**
     * Restarts all reminder alarms
     *
     * @return flow of the tasks that are restarted
     */
    fun restartAlarms(): Flow<List<Task>> = getTasks().take(1).onEach { taskList ->
        taskList.filter {
            // only tasks that are either recurring or not completed yet, and reminder in the future
            it.reminder != null
                    && it.reminder.isAfter(Instant.now())
                    && (it.status == TaskStatus.DEFAULT
                    || (it.status == TaskStatus.DONE && it.isRecurring)
                    )
        }.forEach { task ->
            task.reminder?.let { reminder ->
                reminderManager.startReminder(reminder, task.id)
            }
        }
    }
}