package com.dk.piley.model.calendar

import com.dk.piley.model.task.RecurringTimeRange
import com.dk.piley.model.task.Task
import kotlinx.cinterop.ExperimentalForeignApi
import platform.EventKit.EKAlarm
import platform.EventKit.EKAuthorizationStatusAuthorized
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

@OptIn(ExperimentalForeignApi::class)
class IosReminderSyncManager : TaskCalendarSyncManager {
    private val store = EKEventStore()

    private fun ensureAccess(): Boolean =
        EKEventStore.Companion.authorizationStatusForEntityType(EKEntityType.EKEntityTypeReminder) == EKAuthorizationStatusAuthorized

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
        reminder.notes = task.description

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
}
