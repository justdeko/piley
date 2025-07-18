package com.dk.piley.util

import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.task.RecurringTimeRange
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.model.user.PileMode
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.random.Random
import kotlin.time.Clock

val previewTaskList: List<Task> = listOf(
    Task(
        id = 1,
        title = "Buy groceries",
        description = "Milk, eggs, bread, and vegetables",
        reminder = Clock.System.now().plus(1, DateTimeUnit.HOUR)
    ),
    Task(id = 2, title = "Finish presentation slides"),
    Task(id = 3, title = "Call the plumber"),
    Task(id = 4, title = "Schedule dentist appointment", isRecurring = true),
    Task(id = 5, title = "Exercise"),
    Task(id = 6, title = "Water the plants", status = TaskStatus.DONE),
    Task(id = 7, title = "Respond to emails and messages"),
    Task(id = 8, title = "Clean the house: vacuum, dust, and mop", status = TaskStatus.DONE),
    Task(id = 9, title = "Do the laundry", status = TaskStatus.DONE),
    Task(id = 10, title = "Pay credit card bills"),
    Task(id = 11, title = "Attend online class"),
    Task(id = 12, title = "Plan and prepare meals for the week"),
    Task(
        id = 13,
        title = "Send birthday or anniversary wishes to cousin",
        status = TaskStatus.DONE
    ),
    Task(
        id = 14,
        title = "Review and update your budget or financial plan",
        reminder = Clock.System.now().plus(30, DateTimeUnit.HOUR)
    ),
    Task(id = 15, title = "Buy gift for Lisa", status = TaskStatus.DONE),
    Task(id = 16, title = "Call parents"),
    Task(id = 17, title = "Fix washing machine"),
    Task(id = 18, title = "Practice piano", isRecurring = true),
    Task(id = 19, title = "Meditate", status = TaskStatus.DONE),
)

val previewPileWithTasksList: List<PileWithTasks> = listOf(
    PileWithTasks(Pile(pileId = 1, name = "Daily", pileLimit = 5), previewTaskList.subList(0, 5)),
    PileWithTasks(
        Pile(
            pileId = 3,
            name = "School & Education",
            description = "Tasks related to my studies",
        ), previewTaskList.subList(8, 13)
    ),
    PileWithTasks(
        Pile(pileId = 2, name = "Shopping list", pileMode = PileMode.FIFO),
        previewTaskList.subList(5, 8)
    ),
    PileWithTasks(
        Pile(pileId = 4, name = "Work", pileMode = PileMode.FIFO),
        previewTaskList.subList(13, 17)
    ),
    PileWithTasks(Pile(pileId = 5, name = "Other"), previewTaskList.subList(17, 19))
)

val previewUpcomingTasksList = listOf(
    Pair(
        "Daily",
        Task(
            id = 1,
            title = "Clean room",
            reminder = LocalDateTime.parse("2023-08-04T09:36:24").toInstantWithOffset()
                .plus(1, DateTimeUnit.DAY, utcZone),
            isRecurring = true,
            recurringFrequency = 2,
            recurringTimeRange = RecurringTimeRange.WEEKLY,
            completionTimes = listOf(Clock.System.now())
        )
    ),
    Pair(
        "Shopping List",
        Task(
            id = 2,
            title = "Buy bananas with a very long task title that has a lot of symbols",
            reminder = LocalDateTime.parse("2023-08-04T18:02:24").toInstantWithOffset()
                .plus(2, DateTimeUnit.DAY, utcZone)
        )
    ),
    Pair(
        "Daily",
        Task(
            id = 3,
            title = "Call Dentist",
            reminder = LocalDateTime.parse("2023-08-04T14:01:24").toInstantWithOffset()
                .plus(3, DateTimeUnit.DAY, utcZone)
        )
    ),
    Pair(
        "Daily",
        Task(
            id = 4,
            title = "Completed recurring task",
            reminder = LocalDateTime.parse("2023-08-04T14:01:24").toInstantWithOffset()
                .plus(3, DateTimeUnit.DAY, utcZone),
            completionTimes = listOf(Clock.System.now()),
            isRecurring = true
        )
    )
)

val bigPreviewPile = PileWithTasks(
    Pile(pileId = 1, name = "Daily"),
    previewUpcomingTasksList.map { it.second }
            + previewTaskList.filter { it.status != TaskStatus.DEFAULT }.mapIndexed { index, task ->
        val completionTimesList = List(Random.nextInt(1, 5)) {
            Clock.System.now().minus(index, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
        }
        task.copy(completionTimes = completionTimesList)
    }
)
