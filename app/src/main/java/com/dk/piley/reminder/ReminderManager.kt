package com.dk.piley.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.dk.piley.receiver.ReminderAlarmReceiver
import com.dk.piley.ui.util.toTimestamp
import dagger.hilt.android.qualifiers.ApplicationContext
import org.threeten.bp.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    fun startReminder(
        reminderDateTime: LocalDateTime, taskId: Long
    ) {
        Log.d("ReminderManager", "starting reminder")
        val intent = Intent(context.applicationContext, ReminderAlarmReceiver::class.java).apply {
            action = ReminderAlarmReceiver.ACTION_SHOW
            putExtra(ReminderAlarmReceiver.EXTRA_TASK_ID, taskId)
        }.let { intent ->
            PendingIntent.getBroadcast(
                context.applicationContext,
                taskId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        alarmManager?.setAlarmClock(
            AlarmManager.AlarmClockInfo(reminderDateTime.toTimestamp(), intent), intent
        )
    }

    fun cancelReminder(
        taskId: Long
    ) {
        val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            action = ReminderAlarmReceiver.ACTION_SHOW
        }.let { intent ->
            PendingIntent.getBroadcast(
                context, taskId.toInt(), intent, 0
            )
        }
        alarmManager?.cancel(intent)
    }

}