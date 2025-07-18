package com.dk.piley.model.task

import com.dk.piley.reminder.INotificationManager
import com.dk.piley.reminder.IReminderManager
import com.dk.piley.reminder.getNextReminderTime
import com.dk.piley.reminder.withNewCompletionTime
import com.dk.piley.util.Platform
import com.dk.piley.util.appPlatform
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.getString
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.reminder_complete_action
import piley.composeapp.generated.resources.reminder_custom_delay_action
import piley.composeapp.generated.resources.reminder_delay_action
import kotlin.time.Clock

/**
 * Task repository for performing database operations regarding tasks
 *
 * @property taskDao the task dao providing an interface to the database
 * @property reminderManager entity of the reminder manager to set and cancel reminders
 * @property notificationManager entity of the notification manager to set and cancel notifications
 */
class TaskRepository(
    private val taskDao: TaskDao,
    private val reminderManager: IReminderManager,
    private val notificationManager: INotificationManager,
) {
    fun getTasks(): Flow<List<Task>> = taskDao.getTasks()

    fun getTaskById(taskId: Long): Flow<Task?> = taskDao.getTaskById(taskId)

    /**
     * Insert task and perform additional actions based on task status
     *
     * @param task the task entity
     * @param undo flag signifying whether the action is an undo
     * @return long representing db operation success
     */
    suspend fun insertTaskWithStatus(task: Task, undo: Boolean = false): Long {
        val now = Clock.System.now()
        // update modification time
        var tempTask = if (undo) task else task.copy(modifiedAt = now)
        // add new completion time
        if (task.status == TaskStatus.DONE && !undo) {
            tempTask = tempTask.withNewCompletionTime(now)
        }
        // recreate reminder if undo occurred
        if (task.status == TaskStatus.DEFAULT && undo && task.reminder != null) {
            reminderManager.startReminder(
                reminderTime = task.reminder,
                task = task,
                actionTitles = getReminderActionTitles()
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
    suspend fun insertTask(task: Task): Long {
        val taskId = taskDao.insertTask(task.copy(modifiedAt = Clock.System.now()))
        if (appPlatform == Platform.IOS && task.reminder != null) {
            // for ios, we need to restart the reminder specifically to update task changes
            reminderManager.startReminder(task.reminder, task, getReminderActionTitles())
        }
        return taskId
    }

    suspend fun deleteAllCompletedDeletedTasksForPile(pileId: Long) =
        taskDao.deleteCompletedDeletedForPile(pileId)

    /**
     * Dismiss alarm and notifications for a given task and create new ones if it is recurring
     * also insert the modified task
     *
     * @param task the task entity to dismiss and recreate reminders for and insert
     * @return long representing db operation success
     */
    private suspend fun dismissAlarmAndNotificationAndInsert(task: Task): Long {
        reminderManager.cancelReminder(task.id)
        notificationManager.dismiss(task.id)
        // set next reminder if task is recurring
        if (task.status != TaskStatus.DELETED && task.isRecurring && task.reminder != null) {
            task.getNextReminderTime().let {
                reminderManager.startReminder(
                    reminderTime = it,
                    task = task,
                    actionTitles = getReminderActionTitles()
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
                    && it.reminder > Clock.System.now()
                    && (it.status == TaskStatus.DEFAULT
                    || (it.status == TaskStatus.DONE && it.isRecurring)
                    )
        }.forEach { task ->
            task.reminder?.let { reminder ->
                reminderManager.startReminder(reminder, task, getReminderActionTitles())
            }
        }
    }

    suspend fun delayTask(task: Task, minutes: Long) {
        val newReminderTime = Clock.System.now().plus(minutes, DateTimeUnit.MINUTE)
        // update reminder time in db
        if (task.nowAsReminderTime) {
            insertTask(task.copy(reminder = newReminderTime))
        }
        // start new reminder
        reminderManager.startReminder(newReminderTime, task, getReminderActionTitles())
        notificationManager.dismiss(task.id)
    }

    // TODO see if this is still needed, workaround for iOS
    suspend fun getReminderActionTitles() = Triple(
        getString(Res.string.reminder_delay_action),
        getString(Res.string.reminder_custom_delay_action),
        getString(Res.string.reminder_complete_action)
    )
}