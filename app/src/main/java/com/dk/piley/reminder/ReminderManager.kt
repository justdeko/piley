package com.dk.piley.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.dk.piley.receiver.ReminderAlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import org.threeten.bp.LocalDateTime
import java.util.*
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
        val intent =
            Intent(context.applicationContext, ReminderAlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(
                    context.applicationContext,
                    taskId.toInt(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

        val calendar: Calendar = Calendar.getInstance(Locale.ENGLISH).apply {
            set(Calendar.HOUR_OF_DAY, reminderDateTime.hour)
            set(Calendar.MINUTE, reminderDateTime.minute)
        }
        if (Calendar.getInstance(Locale.ENGLISH)
                .apply { add(Calendar.MINUTE, 1) }.timeInMillis - calendar.timeInMillis > 0
        ) {
            calendar.add(Calendar.DATE, 1)
        }

        alarmManager?.setAlarmClock(
            AlarmManager.AlarmClockInfo(calendar.timeInMillis, intent), intent
        )
    }

    fun cancelReminder(
        taskId: Long
    ) {
        val intent = Intent(context, ReminderAlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                context, taskId.toInt(), intent, 0
            )
        }
        alarmManager?.cancel(intent)
    }

}