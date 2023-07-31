package com.dk.piley.util

import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.model.user.NightMode
import com.dk.piley.model.user.PileMode
import com.dk.piley.model.user.User
import org.threeten.bp.LocalDateTime

val previewTaskList: List<Task> = listOf(
    Task(title = "Buy groceries", description = "Milk, eggs, bread, and vegetables"),
    Task(title = "Finish presentation slides"),
    Task(title = "Call the plumber"),
    Task(title = "Schedule dentist appointment"),
    Task(title = "Exercise"),
    Task(title = "Water the plants", status = TaskStatus.DONE),
    Task(title = "Respond to emails and messages"),
    Task(title = "Clean the house: vacuum, dust, and mop", status = TaskStatus.DONE),
    Task(title = "Do the laundry", status = TaskStatus.DONE),
    Task(title = "Pay credit card bills"),
    Task(title = "Attend online class"),
    Task(title = "Plan and prepare meals for the week"),
    Task(title = "Send birthday or anniversary wishes to cousin", status = TaskStatus.DONE),
    Task(title = "Review and update your budget or financial plan"),
    Task(title = "Buy gift for Lisa", status = TaskStatus.DONE),
    Task(title = "Call parents"),
    Task(title = "Fix washing machine"),
    Task(title = "Practice piano"),
    Task(title = "Meditate", status = TaskStatus.DONE),
)

val previewPileWithTasksList: List<PileWithTasks> = listOf(
    PileWithTasks(Pile(pileId = 1, name = "Daily"), previewTaskList.subList(0, 5)),
    PileWithTasks(
        Pile(pileId = 2, name = "Shopping list", pileMode = PileMode.FIFO),
        previewTaskList.subList(5, 8)
    ),
    PileWithTasks(Pile(pileId = 3, name = "School"), previewTaskList.subList(8, 13)),
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
    Pair("Default", Task(title = "task 1", reminder = LocalDateTime.now().plusDays(1))),
    Pair(
        "Some other Pile",
        Task(title = "task 2", reminder = LocalDateTime.now().plusDays(2))
    ),
    Pair("Default", Task(title = "task 3", reminder = LocalDateTime.now().plusDays(3)))
)
