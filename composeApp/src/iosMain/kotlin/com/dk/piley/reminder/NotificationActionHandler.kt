package com.dk.piley.reminder

import com.dk.piley.Piley
import com.dk.piley.model.navigation.NavigationEvent
import com.dk.piley.model.navigation.NavigationEventRepository
import com.dk.piley.model.task.TaskRepository
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.model.user.UserRepository
import com.dk.piley.ui.nav.taskScreen
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class NotificationActionHandler {

    private val taskRepository: TaskRepository by lazy { Piley.getModule().taskRepository }
    private val userRepository: UserRepository by lazy { Piley.getModule().userRepository }
    private val navigationEventRepository: NavigationEventRepository by lazy { Piley.getModule().navigationEventRepository }

    // TODO fix this not working when app is killed
    @OptIn(DelicateCoroutinesApi::class)
    fun handleNotificationAction(
        actionIdentifier: String,
        taskId: Long,
        withCompletionHandler: () -> Unit
    ) {
        val action = NotificationAction.fromIdentifier(actionIdentifier)
        println("Handling notification action $action for task $taskId")
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
                navigationEventRepository.addNavigationEvent(NavigationEvent("${taskScreen.root}/${taskId}?delay=true"))
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

            // if no action mapping, do default navigation to task
            else -> {
                navigationEventRepository.addNavigationEvent(NavigationEvent("${taskScreen.root}/${taskId}"))
                withCompletionHandler()
            }
        }
    }
}