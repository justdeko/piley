package com.dk.piley.reminder

/**
 * Represents the reminder action type
 *
 */
sealed interface ReminderAction {
    data class Show(val taskId: Long) : ReminderAction
    data class Delay(val taskId: Long) : ReminderAction
    data class CustomDelay(val taskId: Long): ReminderAction
    data class Complete(val taskId: Long) : ReminderAction

    data object BootCompleted : ReminderAction
}
