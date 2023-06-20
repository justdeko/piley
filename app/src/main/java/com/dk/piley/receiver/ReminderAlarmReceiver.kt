package com.dk.piley.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dk.piley.reminder.NotificationExecutor
import com.dk.piley.reminder.ReminderAction
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ReminderAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var taskNotificationExecutor: NotificationExecutor

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("Alarm received with ${intent?.action}")
        val taskId = intent?.getLongExtra(EXTRA_TASK_ID, -1) ?: -1

        when (intent?.action) {
            ACTION_SHOW -> {
                taskNotificationExecutor.execute(ReminderAction.Show(taskId))
            }
            ACTION_COMPLETE -> {
                taskNotificationExecutor.execute(ReminderAction.Complete(taskId))
            }
            ACTION_DELAY -> {
                taskNotificationExecutor.execute(ReminderAction.Delay(taskId))
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                taskNotificationExecutor.execute(ReminderAction.BootCompleted)
            }
        }
    }

    companion object {
        private const val PACKAGE_NAME = "com.dk.intent"

        const val EXTRA_TASK_ID = "$PACKAGE_NAME.extra.TASK_ID"
        const val ACTION_SHOW = "$PACKAGE_NAME.action.ACTION_REMINDER_SHOW"
        const val ACTION_DELAY = "$PACKAGE_NAME.action.ACTION_REMINDER_DELAY"
        const val ACTION_COMPLETE = "$PACKAGE_NAME.action.ACTION_REMINDER_COMPLETE"
    }
}