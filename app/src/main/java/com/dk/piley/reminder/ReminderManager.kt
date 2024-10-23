package com.dk.piley.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import com.dk.piley.receiver.ReminderAlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.datetime.Instant
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reminder manager that handles task reminders
 *
 * @property context generic application context
 */
@Singleton
class ReminderManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    private val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

    /**
     * Start reminder for a task
     *
     * @param reminderTime timestamp representing the reminder time
     * @param taskId task id of the task to show the reminder for
     */
    fun startReminder(
        reminderTime: Instant, taskId: Long
    ) {
        Timber.i("Starting reminder with datetime $reminderTime for task id $taskId")
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
    fun cancelReminder(
        taskId: Long
    ) {
        Timber.i("Cancelling reminder for task id $taskId")
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