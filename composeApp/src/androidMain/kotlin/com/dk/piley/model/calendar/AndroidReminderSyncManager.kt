package com.dk.piley.model.calendar

import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import android.provider.CalendarContract.Reminders
import com.dk.piley.model.task.Task
import java.util.TimeZone

class AndroidReminderSyncManager(private val context: Context) : TaskCalendarSyncManager {

    private val cr get() = context.contentResolver

    override suspend fun addReminder(task: Task) {
        if (task.reminder == null) return
        val calendarId = getPrimaryCalendarId() ?: return

        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, task.reminder.toEpochMilliseconds())
            put(CalendarContract.Events.DTEND, task.reminder.toEpochMilliseconds() + 30 * 60 * 1000)
            put(CalendarContract.Events.TITLE, task.title)
            put(CalendarContract.Events.CALENDAR_ID, calendarId)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            put(CalendarContract.Events.HAS_ALARM, 1)
            put(CalendarContract.Events.DESCRIPTION, task.description)
            if (task.isRecurring) {
                put(CalendarContract.Events.RRULE, task.generateRRule())
            }
        }

        val uri = cr.insert(CalendarContract.Events.CONTENT_URI, values) ?: return
        val eventId = uri.lastPathSegment?.toLongOrNull() ?: return

        val reminderValues = ContentValues().apply {
            put(Reminders.EVENT_ID, eventId)
            put(Reminders.MINUTES, 10)
            put(Reminders.METHOD, Reminders.METHOD_ALERT)
        }
        cr.insert(Reminders.CONTENT_URI, reminderValues)
    }

    private fun getPrimaryCalendarId(): Long? {
        val projection =
            arrayOf(CalendarContract.Calendars._ID, CalendarContract.Calendars.IS_PRIMARY)
        val uri = CalendarContract.Calendars.CONTENT_URI
        cr.query(uri, projection, null, null, null)?.use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getLong(0)
                val isPrimary = cursor.getInt(1) == 1
                if (isPrimary) return id
            }
        }
        return null
    }
}
