package com.dk.piley.model.calendar

import com.dk.piley.model.task.RecurringTimeRange
import com.dk.piley.model.task.Task
import kotlinx.cinterop.ExperimentalForeignApi
import platform.EventKit.EKAlarm
import platform.EventKit.EKEntityType
import platform.EventKit.EKEventStore
import platform.EventKit.EKRecurrenceFrequency
import platform.EventKit.EKRecurrenceRule
import platform.EventKit.EKReminder
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSince1970
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalForeignApi::class)
class IosReminderSyncManager : TaskCalendarSyncManager {
    private val store = EKEventStore()

    private suspend fun ensureAccess(): Boolean = suspendCoroutine { cont ->
        store.requestAccessToEntityType(EKEntityType.EKEntityTypeReminder) { granted, _ ->
            cont.resume(granted)
        }
    }

    override suspend fun addReminder(task: Task) {
        if (!ensureAccess() || task.reminder == null) return

        val reminder = EKReminder.reminderWithEventStore(store)
        reminder.title = task.title
        reminder.calendar = store.defaultCalendarForNewReminders()

        val dateComponents = NSCalendar.currentCalendar.components(
            NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay or
                    NSCalendarUnitHour or NSCalendarUnitMinute,
            fromDate = NSDate.dateWithTimeIntervalSince1970(task.reminder.toEpochMilliseconds() / 1000.0)
        )

        reminder.dueDateComponents = dateComponents
        reminder.notes = "taskId:${task.id}"

        val alarm = EKAlarm.alarmWithAbsoluteDate(
            NSDate.dateWithTimeIntervalSince1970(task.reminder.toEpochMilliseconds() / 1000.0)
        )
        reminder.addAlarm(alarm)

        if (task.isRecurring && task.isRecurring) {
            reminder.recurrenceRules = listOf(
                EKRecurrenceRule(
                    recurrenceWithFrequency = when (task.recurringTimeRange) {
                        RecurringTimeRange.DAILY -> EKRecurrenceFrequency.EKRecurrenceFrequencyDaily
                        RecurringTimeRange.WEEKLY -> EKRecurrenceFrequency.EKRecurrenceFrequencyWeekly
                        RecurringTimeRange.MONTHLY -> EKRecurrenceFrequency.EKRecurrenceFrequencyMonthly
                        RecurringTimeRange.YEARLY -> EKRecurrenceFrequency.EKRecurrenceFrequencyYearly
                    },
                    interval = task.recurringFrequency.toLong(),
                    end = null
                )
            )
        }

        store.saveReminder(reminder, commit = true, error = null)
    }

    override suspend fun removeReminder(task: Task) {
        if (!ensureAccess()) return

        val predicate = store.predicateForRemindersInCalendars(null)

        val matching = suspendCoroutine { cont ->
            store.fetchRemindersMatchingPredicate(predicate) { remindersAny ->
                val reminders = remindersAny?.filterIsInstance<EKReminder>() ?: emptyList()
                val filtered = reminders.filter { reminder ->
                    reminder.notes?.contains("taskId:${task.id}") == true
                }
                cont.resume(filtered)
            }
        }

        matching.forEach { reminder ->
            store.removeReminder(reminder, commit = true, error = null)
        }
    }


}
