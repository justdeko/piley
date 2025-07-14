package com.dk.piley.reminder

import com.dk.piley.model.task.Task
import com.dk.piley.util.toLocalDateTime
import kotlinx.datetime.toNSDateComponents
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
import kotlin.time.Instant

class ReminderManager : IReminderManager {
    override suspend fun startReminder(reminderTime: Instant, task: Task, actionTitles: Triple<String, String, String>) {
        val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
        // set actions and categories
        val delayAction = UNNotificationAction.actionWithIdentifier(
            identifier = NotificationAction.DELAY.identifier,
            title = actionTitles.first,
            options = UNNotificationActionOptionNone
        )
        val delayByAction = UNNotificationAction.actionWithIdentifier(
            identifier = NotificationAction.DELAY_BY.identifier,
            title = actionTitles.second,
            options = UNNotificationActionOptionForeground
        )
        val doneAction = UNNotificationAction.actionWithIdentifier(
            identifier = NotificationAction.DONE.identifier,
            title = actionTitles.third,
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