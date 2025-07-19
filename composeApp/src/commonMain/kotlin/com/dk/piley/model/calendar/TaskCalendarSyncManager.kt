package com.dk.piley.model.calendar

import com.dk.piley.model.task.RecurringTimeRange
import com.dk.piley.model.task.Task

interface TaskCalendarSyncManager {
    suspend fun addReminder(task: Task)

    fun Task.generateRRule(): String {
        val freq = when (recurringTimeRange) {
            RecurringTimeRange.DAILY -> "DAILY"
            RecurringTimeRange.WEEKLY -> "WEEKLY"
            RecurringTimeRange.MONTHLY -> "MONTHLY"
            RecurringTimeRange.YEARLY -> "YEARLY"
        }

        return "FREQ=$freq;INTERVAL=$recurringFrequency"
    }
}