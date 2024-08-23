package com.dk.piley.util

import android.content.Context
import com.dk.piley.R
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.task.RecurringTimeRange
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.toRecurringTimeRange
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.time.Instant
import java.time.LocalDateTime

class ReminderUtilTest {

    private val sampleDateTime: LocalDateTime = LocalDateTime.of(2023, 8, 9, 13, 14)

    private lateinit var mockContext: Context

    @Before
    fun setup() {
        mockContext = mock {
            on { resources } doReturn mock()
            on { resources.getStringArray(R.array.time_range) } doReturn listOf(
                "Day",
                "Week",
                "Month",
                "Year"
            ).toTypedArray()
        }
    }

    @Test
    fun reminderPlusTime() {
        val expectedTomorrow = sampleDateTime.plusDays(1)
        val expectedTwoWeeks = sampleDateTime.plusWeeks(2)
        val expectedThreeMonths = sampleDateTime.plusMonths(3)
        val expectedTwoYears = sampleDateTime.plusYears(2)
        assertEquals(
            expectedTomorrow,
            reminderPlusTime(sampleDateTime, RecurringTimeRange.DAILY, 1)
        )
        assertEquals(
            expectedTwoWeeks,
            reminderPlusTime(sampleDateTime, RecurringTimeRange.WEEKLY, 2)
        )
        assertEquals(
            expectedThreeMonths,
            reminderPlusTime(sampleDateTime, RecurringTimeRange.MONTHLY, 3)
        )
        assertEquals(
            expectedTwoYears,
            reminderPlusTime(sampleDateTime, RecurringTimeRange.YEARLY, 2)
        )
    }

    @Test
    fun testGetNextReminderTime() {
        val sampleTask = Task(
            recurringTimeRange = RecurringTimeRange.WEEKLY,
            recurringFrequency = 2,
            nowAsReminderTime = true
        )
        val now = Instant.now()
        val expectedTime = now.toLocalDateTime().plusWeeks(2).toInstantWithOffset()
        val reminderTime = sampleTask.getNextReminderTime(now)
        assertEquals(expectedTime, reminderTime)
    }

    @Test
    fun testGetNextReminderTimeWithPastAsReminder() {
        val now = Instant.now()
        val pastReminder = now.toLocalDateTime().minusDays(2).toInstantWithOffset()
        val sampleTask = Task(
            recurringTimeRange = RecurringTimeRange.WEEKLY,
            recurringFrequency = 2,
            nowAsReminderTime = false,
            reminder = pastReminder
        )
        val expectedTime = now.toLocalDateTime().plusWeeks(2).minusDays(2).toInstantWithOffset()
        val reminderTime = sampleTask.getNextReminderTime()
        assertEquals(expectedTime, reminderTime)
    }

    @Test
    fun testGetNextReminderTimeWithLongAgoAsReminder() {
        val now = Instant.now()
        val pastReminder = now.toLocalDateTime().minusDays(2).minusWeeks(9).toInstantWithOffset()
        val sampleTask = Task(
            recurringTimeRange = RecurringTimeRange.WEEKLY,
            recurringFrequency = 2,
            nowAsReminderTime = false,
            reminder = pastReminder
        )
        val expectedTime = now.toLocalDateTime().plusWeeks(1).minusDays(2).toInstantWithOffset()
        val reminderTime = sampleTask.getNextReminderTime()
        assertEquals(expectedTime, reminderTime)
    }

    @Test
    fun toRecurringTimeRange() {
        val dailyRange = "Day".toRecurringTimeRange(mockContext)
        assertEquals(RecurringTimeRange.DAILY, dailyRange)
        val weeklyRange = "Week".toRecurringTimeRange(mockContext)
        assertEquals(RecurringTimeRange.WEEKLY, weeklyRange)
        val monthlyRange = "Month".toRecurringTimeRange(mockContext)
        assertEquals(RecurringTimeRange.MONTHLY, monthlyRange)
        val yearlyRange = "Year".toRecurringTimeRange(mockContext)
        assertEquals(RecurringTimeRange.YEARLY, yearlyRange)
        val nanRange = "abc".toRecurringTimeRange(mockContext)
        assertEquals(RecurringTimeRange.DAILY, nanRange)
    }

    @Test
    fun getPileNameForTaskId() {
        val sampleId = 3L
        val expectedPileName = "C"
        val pilesWithTasks =
            listOf(
                PileWithTasks(Pile(name = "A"), listOf(Task())),
                PileWithTasks(Pile(name = "B"), listOf(Task())),
                PileWithTasks(Pile(name = expectedPileName), listOf(Task(id = sampleId)))
            )
        assertEquals(expectedPileName, getPileNameForTaskId(sampleId, pilesWithTasks))
    }
}