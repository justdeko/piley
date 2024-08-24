package com.dk.piley.reminder

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Notification executor that executes actions regarding notifications
 *
 * @property actionHandler instance of the reminder action handler which is used to determine the notification action
 */
@Singleton
@OptIn(DelicateCoroutinesApi::class)
class NotificationExecutor @Inject constructor(
    private val actionHandler: IReminderActionHandler
) {
    fun execute(action: ReminderAction) {
        when (action) {
            is ReminderAction.Show -> {
                GlobalScope.launch {
                    actionHandler.show(action.taskId).collect()
                }
            }
            is ReminderAction.BootCompleted -> {
                GlobalScope.launch {
                    actionHandler.restartAll().collect()
                }
            }
            is ReminderAction.Complete -> {
                GlobalScope.launch(Dispatchers.IO) {
                    actionHandler.complete(action.taskId).collect()
                }
            }
            is ReminderAction.Delay -> {
                GlobalScope.launch {
                    actionHandler.delay(action.taskId).collect()
                }
            }
            is ReminderAction.CustomDelay -> {
                GlobalScope.launch {
                    actionHandler.customDelay(action.taskId).collect()
                }
            }
        }
    }
}
