package com.dk.piley.util

import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.task.RecurringTimeRange
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.model.user.NightMode
import com.dk.piley.model.user.PileMode
import com.dk.piley.model.user.User
import java.time.LocalDateTime

val previewTaskList: List<Task> = listOf(
    Task(id = 1, title = "Buy groceries", description = "Milk, eggs, bread, and vegetables"),
    Task(id = 2, title = "Finish presentation slides"),
    Task(id = 3, title = "Call the plumber"),
    Task(id = 4, title = "Schedule dentist appointment"),
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
    Task(id = 14, title = "Review and update your budget or financial plan"),
    Task(id = 15, title = "Buy gift for Lisa", status = TaskStatus.DONE),
    Task(id = 16, title = "Call parents"),
    Task(id = 17, title = "Fix washing machine"),
    Task(id = 18, title = "Practice piano"),
    Task(id = 19, title = "Meditate", status = TaskStatus.DONE),
)

val previewPileWithTasksList: List<PileWithTasks> = listOf(
    PileWithTasks(Pile(pileId = 1, name = "Daily"), previewTaskList.subList(0, 5)),
    PileWithTasks(Pile(pileId = 3, name = "School & Education"), previewTaskList.subList(8, 13)),
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

val previewUser: User = User(
    email = "paul@something.com",
    name = "Paul",
    password = "123456",
    selectedPileId = 1,
    defaultPileId = 1,
    lastBackup = null,
    lastBackupQuery = null,
    nightMode = NightMode.SYSTEM,
    dynamicColorOn = true,
    pileMode = PileMode.FREE,
    defaultReminderDelay = 15,
    defaultBackupFrequency = 2,
    autoHideKeyboard = true,
    isOffline = false
)

val previewUpcomingTasksList = listOf(
    Pair(
        "Daily",
        Task(
            title = "Clean room",
            reminder = LocalDateTime.parse("2023-08-04T09:36:24").plusDays(1).toInstant(),
            isRecurring = true,
            recurringFrequency = 2,
            recurringTimeRange = RecurringTimeRange.WEEKLY
        )
    ),
    Pair(
        "Shopping List",
        Task(
            title = "Buy bananas with a very long task title that has a lot of symbols",
            reminder = LocalDateTime.parse("2023-08-04T18:02:24").plusDays(2).toInstant()
        )
    ),
    Pair(
        "Daily",
        Task(
            title = "Call Dentist",
            reminder = LocalDateTime.parse("2023-08-04T14:01:24").plusDays(3).toInstant()
        )
    )
)
