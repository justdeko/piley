package com.dk.piley.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import com.dk.piley.receiver.ReminderAlarmReceiver
import com.dk.piley.util.toTimestamp
import dagger.hilt.android.qualifiers.ApplicationContext
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    private val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

    fun startReminder(
        reminderDateTime: LocalDateTime, taskId: Long
    ) {
        Timber.i("starting reminder with datetime $reminderDateTime for task id $taskId")
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
                it, AlarmManager.RTC_WAKEUP, reminderDateTime.toTimestamp(), intent
            )
        }
    }

    fun cancelReminder(
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