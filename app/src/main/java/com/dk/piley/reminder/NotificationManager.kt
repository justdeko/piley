package com.dk.piley.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.dk.piley.MainActivity
import com.dk.piley.R
import com.dk.piley.model.task.Task
import com.dk.piley.receiver.ReminderAlarmReceiver
import com.dk.piley.ui.nav.DEEPLINK_ROOT
import com.dk.piley.ui.nav.taskScreen
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Notification manager for handling notification actions and interactions
 *
 * @property context generic context of the application
 */
@Singleton
class NotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

    init {
        createNotificationChannel()
    }

    /**
     * Create notification channel for sending reminder notifications
     *
     */
    private fun createNotificationChannel() {
        NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.reminder_notification_channel_title),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            this.description =
                context.getString(R.string.reminder_notification_channel_description)
            enableLights(true)
            lightColor = ContextCompat.getColor(context, R.color.md_theme_light_primary)
            enableVibration(true)
            notificationManager?.createNotificationChannel(this)
        }
    }

    /**
     * Show notification for given task
     *
     * @param task the task to show the notification for
     * @param pileName the name of the parent pile
     */
    fun showNotification(task: Task, pileName: String?) {
        val taskDetailIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(
                Intent(
                    Intent.ACTION_VIEW,
                    "$DEEPLINK_ROOT/${taskScreen.root}/${task.id}".toUri(),
                    context,
                    MainActivity::class.java
                )
            )
            getPendingIntent(task.id.toInt(), FLAGS)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification).setContentTitle(task.title)
            .apply {
                // set content of second row and content of expanded notification
                if (pileName != null) {
                    setContentText(pileName)
                }
                if (task.description.isNotBlank()) {
                    setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(task.description)
                    )
                }
            }
            .setColor(ContextCompat.getColor(context, R.color.md_theme_light_primary))
            .setContentIntent(taskDetailIntent)
            .setAutoCancel(true)
            .setShowWhen(false)
            .addAction(getNotificationAction(task.id, NotificationActionType.Delay))
            .addAction(getNotificationAction(task.id, NotificationActionType.CustomDelay))
            .addAction(getNotificationAction(task.id, NotificationActionType.Done))
            .build()
        notificationManager?.notify(task.id.toInt(), notification)
    }

    /**
     * Get notification action (done or delay) based on the task id and whether it is a done action
     *
     * @param taskId task id to perform the action
     * @param notificationActionType the notification action type
     * @return notification action
     */
    private fun getNotificationAction(
        taskId: Long,
        notificationActionType: NotificationActionType
    ): NotificationCompat.Action {
        val actionTitle = when (notificationActionType) {
            NotificationActionType.Done -> context.getString(R.string.reminder_complete_action)
            NotificationActionType.Delay -> context.getString(R.string.reminder_delay_action)
            NotificationActionType.CustomDelay -> context.getString(R.string.reminder_custom_delay_action)
        }
        // action intent
        val receiverIntent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            action = when (notificationActionType) {
                NotificationActionType.Done -> ReminderAlarmReceiver.ACTION_COMPLETE
                NotificationActionType.Delay -> ReminderAlarmReceiver.ACTION_DELAY
                NotificationActionType.CustomDelay -> ReminderAlarmReceiver.ACTION_CUSTOM_DELAY
            }
            putExtra(ReminderAlarmReceiver.EXTRA_TASK_ID, taskId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            receiverIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val delayReminderIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(
                Intent(
                    Intent.ACTION_VIEW,
                    "$DEEPLINK_ROOT/${taskScreen.root}/${taskId}?delay=true".toUri(),
                    context,
                    MainActivity::class.java
                )
            )
            getPendingIntent(taskId.toInt(), FLAGS)
        }
        val intent = if (notificationActionType == NotificationActionType.CustomDelay) {
            delayReminderIntent
        } else {
            pendingIntent
        }
        return NotificationCompat.Action(0, actionTitle, intent)
    }

    fun dismiss(taskId: Long) {
        notificationManager?.cancel(taskId.toInt())
    }


    companion object {
        private const val FLAGS = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        private const val CHANNEL_ID = "channel_reminder"
    }
}