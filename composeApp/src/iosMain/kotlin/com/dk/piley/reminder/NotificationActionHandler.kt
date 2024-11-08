package com.dk.piley.reminder

import com.dk.piley.Piley
import com.dk.piley.model.task.TaskRepository
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.model.user.UserRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class NotificationActionHandler {

    private val taskRepository: TaskRepository by lazy { Piley.getModule().taskRepository }
    private val userRepository: UserRepository by lazy { Piley.getModule().userRepository }

    @OptIn(DelicateCoroutinesApi::class)
    fun handleNotificationAction(
        actionIdentifier: String,
        taskId: Long,
        withCompletionHandler: () -> Unit
    ) {
        println("Handling notification action $actionIdentifier for task $taskId")
        when (actionIdentifier) {
            "DELAY_ACTION" -> {
                GlobalScope.launch(Dispatchers.Main) {
                    taskRepository.getTaskById(taskId).firstOrNull()?.let { task ->
                        userRepository.getSignedInUser().first()?.let { user ->
                            val delayInMinutes = calculateDelayDuration(
                                delayRange = user.defaultReminderDelayRange,
                                delayDurationIndex = user.defaultReminderDelayIndex
                            )
                            taskRepository.delayTask(task, delayInMinutes)
                        }
                    }
                    withCompletionHandler()
                }
            }

            "DELAY_BY_ACTION" -> {
                // TODO implement delay by action
                withCompletionHandler()
            }

            "DONE_ACTION" -> {
                // set task to done
                GlobalScope.launch(Dispatchers.Main) {
                    taskRepository.getTaskById(taskId).firstOrNull()?.let { task ->
                        taskRepository.insertTaskWithStatus(
                            task.copy(status = TaskStatus.DONE)
                        )
                    }
                    withCompletionHandler()
                }
            }
        }
    }
}