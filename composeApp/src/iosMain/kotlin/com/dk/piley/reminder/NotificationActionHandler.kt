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

    // TODO fix this not working when app is killed
    @OptIn(DelicateCoroutinesApi::class)
    fun handleNotificationAction(
        actionIdentifier: String,
        taskId: Long,
        withCompletionHandler: () -> Unit
    ) {
        val action = NotificationAction.fromIdentifier(actionIdentifier)
        println("Handling notification action $actionIdentifier for task $taskId")
        when (action) {
            NotificationAction.DELAY -> {
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

            NotificationAction.DELAY_BY -> {

                withCompletionHandler()
            }

            NotificationAction.DONE -> {
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