package com.dk.piley.util

import android.content.Context
import com.dk.piley.R
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

class StatisticsHelperTest {

    private lateinit var mockContext: Context

    @Before
    fun setup() {
        mockContext = mock {
            on { getString(R.string.no_pile) } doReturn "None"
        }
    }

    private val samplePile: PileWithTasks =
        PileWithTasks(
            Pile(name = "sample pile name"),
            listOf(
                Task(
                    status = TaskStatus.DONE,
                    createdAt = LocalDateTime.now().minusDays(3).toInstantWithOffset(),
                    completionTimes = listOf(LocalDateTime.now().toInstantWithOffset())
                ),
                Task(
                    status = TaskStatus.DONE,
                    createdAt = LocalDateTime.now().minusDays(3).toInstantWithOffset(),
                    completionTimes = listOf(
                        LocalDateTime.now().minusDays(2).toInstantWithOffset(),
                        LocalDateTime.now().minusDays(2).toInstantWithOffset()
                    ),
                    averageCompletionTimeInHours = 24
                ),
                Task(
                    createdAt = LocalDateTime.now().minusDays(6).toInstantWithOffset(),
                    status = TaskStatus.DONE, completionTimes = listOf(
                        LocalDateTime.now().minusDays(5).toInstantWithOffset(),
                        LocalDateTime.now().minusDays(4).toInstantWithOffset()
                    ),
                    averageCompletionTimeInHours = 24
                ),
                Task(
                    createdAt = LocalDateTime.now().minusDays(8).toInstantWithOffset(),
                    status = TaskStatus.DONE, completionTimes = listOf(
                        LocalDateTime.now().minusDays(7).toInstantWithOffset(),
                        LocalDateTime.now().minusDays(5).toInstantWithOffset()
                    ),
                    averageCompletionTimeInHours = 48
                ),
                Task(
                    reminder = Instant.now().toLocalDateTime(utcZoneId).plusDays(2)
                        .toInstantWithOffset()
                ),
                Task(
                    reminder = Instant.now().toLocalDateTime(utcZoneId).plusDays(3)
                        .toInstantWithOffset()
                )
            )
        )

    private fun getExpectedFrequencyMap(): MutableMap<LocalDate, Int> {
        val dateNow = LocalDateTime.now().toLocalDate()
        val expectedFrequencyMap = mutableMapOf<LocalDate, Int>()
        expectedFrequencyMap[dateNow] = 1
        expectedFrequencyMap[dateNow.minusDays(2)] = 2
        expectedFrequencyMap[dateNow.minusDays(4)] = 1
        expectedFrequencyMap[dateNow.minusDays(5)] = 2
        expectedFrequencyMap[dateNow.minusDays(7)] = 1
        return expectedFrequencyMap
    }

    private fun getExpectedMapWithZeros(): MutableMap<LocalDate, Int> {
        val expectedMapWithZeros = getExpectedFrequencyMap()
        val dateNow = LocalDateTime.now().toLocalDate()
        expectedMapWithZeros[dateNow.minusDays(1)] = 0
        expectedMapWithZeros[dateNow.minusDays(3)] = 0
        expectedMapWithZeros[dateNow.minusDays(6)] = 0
        expectedMapWithZeros.remove(dateNow.minusDays(7))
        return expectedMapWithZeros
    }

    @Test
    fun pileFrequenciesForDates() {
        val frequencies = pileFrequenciesForDates(samplePile)
        assertEquals(getExpectedFrequencyMap(), frequencies)
    }

    @Test
    fun pileFrequenciesForDatesWithZeros() {
        val frequenciesWithZeros =
            pileFrequenciesForDatesWithZerosForLast7Days(getExpectedFrequencyMap())
        assertEquals(getExpectedMapWithZeros(), frequenciesWithZeros)

    }

    @Test
    fun getCompletedTasksForWeekValues() {
        val expectedList = listOf(1, 0, 2, 0, 1, 2, 0).reversed()
        val completedTaskValues = getCompletedTasksForWeekValues(samplePile)
        assertEquals(expectedList, completedTaskValues)
    }

    @Test
    fun getUpcomingTasks() {
        val expectedUpcomingTasks = listOf(
            Pair("sample pile name", samplePile.tasks[4]),
            Pair("other pile name", samplePile.tasks[4]),
            Pair("sample pile name", samplePile.tasks[5]),
            Pair("other pile name", samplePile.tasks[5]),
        )
        val testPiles = listOf(
            samplePile,
            samplePile.copy(pile = samplePile.pile.copy(name = "other pile name"))
        )
        val upcomingTasks = getUpcomingTasks(testPiles)
        assertEquals(expectedUpcomingTasks, upcomingTasks)
    }

    @Test
    fun getBiggestPileName() {
        val emptyPiles = emptyList<PileWithTasks>()
        assertEquals("None", getBiggestPileName(emptyPiles, mockContext))
        val pilesWithNoTasks = listOf(samplePile.copy(tasks = emptyList()))
        assertEquals("sample pile name", getBiggestPileName(pilesWithNoTasks, mockContext))
        val secondPileWithMoreTasks = listOf(
            samplePile.copy(tasks = emptyList()),
            samplePile.copy(pile = samplePile.pile.copy(name = "other pile"))
        )
        val twoPiles = pilesWithNoTasks + secondPileWithMoreTasks
        assertEquals("other pile", getBiggestPileName(twoPiles, mockContext))

    }

    @Test
    fun getAverageTaskCompletionInHours() {
        val expectedValue = 16L
        val averageCompletionDuration = getAverageTaskCompletionInHours(samplePile.tasks)
        assertEquals(expectedValue, averageCompletionDuration)
        // empty list
        val emptyCompletionDuration = getAverageTaskCompletionInHours(emptyList())
        assertEquals(0, emptyCompletionDuration)
    }

    @Test
    fun taskWithNewCompletionTime() {
        val dateNow = LocalDateTime.now()

        val taskWithSomeCompletionTimes = Task(
            reminder = dateNow.minusDays(1).toInstantWithOffset(),
            completionTimes = listOf(dateNow.minusDays(1).toInstantWithOffset()),
            averageCompletionTimeInHours = 48
        )
        val newTask = taskWithSomeCompletionTimes.withNewCompletionTime()
        assertEquals(36, newTask.averageCompletionTimeInHours)

        val taskWithNoCompletionTimes =
            Task(reminder = dateNow.minusHours(32).toInstantWithOffset())
        val newTaskEmpty = taskWithNoCompletionTimes.withNewCompletionTime()
        assertEquals(32, newTaskEmpty.averageCompletionTimeInHours)

        val taskWithNoReminder = Task(createdAt = dateNow.minusDays(1).toInstantWithOffset())
        val newTaskWitNoReminder = taskWithNoReminder.withNewCompletionTime()
        assertEquals(24, newTaskWitNoReminder.averageCompletionTimeInHours)
    }
}