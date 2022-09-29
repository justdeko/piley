package com.dk.piley.reminder

sealed class ReminderAction {
    data class Show(val taskId: Long) : ReminderAction()
    data class Delay(val taskId: Long) : ReminderAction()
    data class Complete(val taskId: Long) : ReminderAction()

    object BootCompleted : ReminderAction()
}
