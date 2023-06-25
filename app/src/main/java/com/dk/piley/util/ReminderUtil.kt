package com.dk.piley.util

import com.dk.piley.model.task.RecurringTimeRange
import com.dk.piley.model.task.RecurringTimeRange.DAILY
import com.dk.piley.model.task.RecurringTimeRange.MONTHLY
import com.dk.piley.model.task.RecurringTimeRange.WEEKLY
import com.dk.piley.model.task.RecurringTimeRange.YEARLY
import org.threeten.bp.LocalDateTime

fun getNextReminderTime(
    lastReminder: LocalDateTime,
    recurringTimeRange: RecurringTimeRange,
    recurringFrequency: Int
): LocalDateTime = when (recurringTimeRange) {
    DAILY -> lastReminder.plusDays(recurringFrequency.toLong())
    WEEKLY -> lastReminder.plusWeeks(recurringFrequency.toLong())
    MONTHLY -> lastReminder.plusMonths(recurringFrequency.toLong())
    YEARLY -> lastReminder.plusYears(recurringFrequency.toLong())
}