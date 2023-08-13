package com.dk.piley.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
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

@Singleton
class NotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
    }

    fun showNotification(task: Task, pileName: String?) {
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val taskDetailIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(
                Intent(
                    Intent.ACTION_VIEW,
                    "$DEEPLINK_ROOT/${taskScreen.root}/${task.id}".toUri(),
                    context,
                    MainActivity::class.java
                )
            )
            getPendingIntent(OPEN_TASK_CODE, flags)
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
            .addAction(getNotificationAction(task.id))
            .addAction(getNotificationAction(task.id, true))
            .build()
        notificationManager?.notify(task.id.toInt(), notification)
    }

    private fun getNotificationAction(
        taskId: Long, isDoneAction: Boolean = false
    ): NotificationCompat.Action {
        val actionTitle =
            if (isDoneAction) {
                context.getString(R.string.reminder_complete_action)
            } else context.getString(
                R.string.reminder_delay_action
            )
        val requestCode = if (isDoneAction) COMPLETE_CODE else DELAY_CODE
        // action intent
        val receiverIntent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            action =
                if (isDoneAction) ReminderAlarmReceiver.ACTION_COMPLETE else ReminderAlarmReceiver.ACTION_DELAY
            putExtra(ReminderAlarmReceiver.EXTRA_TASK_ID, taskId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            receiverIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Action(0, actionTitle, pendingIntent)
    }

    fun dismiss(taskId: Long) {
        notificationManager?.cancel(taskId.toInt())
    }


    companion object {
        private const val OPEN_TASK_CODE = 1
        private const val DELAY_CODE = 2
        private const val COMPLETE_CODE = 3
        private const val CHANNEL_ID = "channel_reminder"
    }
}