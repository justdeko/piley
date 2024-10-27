package com.dk.piley.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import com.dk.piley.receiver.ReminderAlarmReceiver
import kotlinx.datetime.Instant

/**
 * Reminder manager that handles task reminders
 *
 * @property context generic application context
 */
class ReminderManager(private val context: Context): IReminderManager {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    private val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

    /**
     * Start reminder for a task
     *
     * @param reminderTime timestamp representing the reminder time
     * @param taskId task id of the task to show the reminder for
     */
    override fun startReminder(
        reminderTime: Instant, taskId: Long
    ) {
        val intent = Intent(context.applicationContext, ReminderAlarmReceiver::class.java).apply {
            action = ReminderAlarmReceiver.ACTION_SHOW
            putExtra(ReminderAlarmReceiver.EXTRA_TASK_ID, taskId)
        }.let { intent ->
            PendingIntent.getBroadcast(
                context.applicationContext, taskId.toInt(), intent, flags
            )
        }

        alarmManager?.let {
            AlarmManagerCompat.setAndAllowWhileIdle(
                it, AlarmManager.RTC_WAKEUP, reminderTime.toEpochMilliseconds(), intent
            )
        }
    }

    /**
     * Cancel reminder for a task
     *
     * @param taskId task id of the task to cancel the reminder for
     */
    override fun cancelReminder(
        taskId: Long
    ) {
        val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            action = ReminderAlarmReceiver.ACTION_SHOW
        }.let { intent ->
            PendingIntent.getBroadcast(
                context, taskId.toInt(), intent, flags
            )
        }
        alarmManager?.cancel(intent)
    }

}