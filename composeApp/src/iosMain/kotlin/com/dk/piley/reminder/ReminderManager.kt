package com.dk.piley.reminder

import com.dk.piley.model.task.Task
import com.dk.piley.util.toLocalDateTime
import kotlinx.datetime.Instant
import kotlinx.datetime.toNSDateComponents
import org.jetbrains.compose.resources.getString
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.reminder_complete_action
import piley.composeapp.generated.resources.reminder_custom_delay_action
import piley.composeapp.generated.resources.reminder_delay_action
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationAction
import platform.UserNotifications.UNNotificationActionOptionForeground
import platform.UserNotifications.UNNotificationActionOptionNone
import platform.UserNotifications.UNNotificationCategory
import platform.UserNotifications.UNNotificationCategoryOptionNone
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter

class ReminderManager : IReminderManager {
    override suspend fun startReminder(reminderTime: Instant, task: Task) {
        val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
        // set actions and categories
        val delayAction = UNNotificationAction.actionWithIdentifier(
            identifier = NotificationAction.DELAY.identifier,
            title = getString(Res.string.reminder_delay_action),
            options = UNNotificationActionOptionNone
        )
        val delayByAction = UNNotificationAction.actionWithIdentifier(
            identifier = NotificationAction.DELAY_BY.identifier,
            title = getString(Res.string.reminder_custom_delay_action),
            options = UNNotificationActionOptionForeground
        )
        val doneAction = UNNotificationAction.actionWithIdentifier(
            identifier = NotificationAction.DONE.identifier,
            title = getString(Res.string.reminder_complete_action),
            options = UNNotificationActionOptionNone
        )
        // Create category with actions
        val category = UNNotificationCategory.categoryWithIdentifier(
            identifier = ACTION_CATEGORY,
            actions = listOf(delayAction, delayByAction, doneAction),
            intentIdentifiers = listOf<Any>(),
            options = UNNotificationCategoryOptionNone
        )
        // Register the category with the notification center
        notificationCenter.setNotificationCategories(setOf(category))
        val content = UNMutableNotificationContent().apply {
            setTitle(task.title)
            setBody(task.description)
            setSound(UNNotificationSound.defaultSound)
            setCategoryIdentifier(ACTION_CATEGORY)
        }
        val notificationTrigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            reminderTime.toLocalDateTime().toNSDateComponents(),
            false
        )
        val request = UNNotificationRequest.requestWithIdentifier(
            task.id.toString(),
            content,
            notificationTrigger
        )
        println("attempting to start notification for time $reminderTime")
        notificationCenter.addNotificationRequest(request) {
            it?.let {
                println(it)
            }
        }
    }

    override fun cancelReminder(taskId: Long) {
        UNUserNotificationCenter.currentNotificationCenter()
            .removePendingNotificationRequestsWithIdentifiers(
                listOf(taskId.toString())
            )
    }
}

private const val ACTION_CATEGORY = "REMINDER_NOTIFICATION_ACTIONS"

enum class NotificationAction(val identifier: String) {
    DELAY("DELAY_ACTION"),
    DELAY_BY("DELAY_BY_ACTION"),
    DONE("DONE_ACTION");

    companion object {
        fun fromIdentifier(identifier: String): NotificationAction? {
            return entries.firstOrNull { it.identifier == identifier }
        }
    }
}