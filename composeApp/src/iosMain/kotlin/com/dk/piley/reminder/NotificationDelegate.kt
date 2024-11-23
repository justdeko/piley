package com.dk.piley.reminder

import com.dk.piley.Piley
import com.dk.piley.ui.common.NOTIFICATION_PERMISSIONS
import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationPresentationOptions
import platform.UserNotifications.UNNotificationResponse
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject

class NotificationDelegate : UNUserNotificationCenterDelegateProtocol, NSObject() {
    private val notificationActionHandler by lazy { Piley.notificationActionHandler }

    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        didReceiveNotificationResponse: UNNotificationResponse,
        withCompletionHandler: () -> Unit
    ) {
        val actionIdentifier = didReceiveNotificationResponse.actionIdentifier
        val taskId = didReceiveNotificationResponse.notification.request.identifier.toLong()
        notificationActionHandler.handleNotificationAction(
            actionIdentifier,
            taskId,
            withCompletionHandler
        )
    }

    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        willPresentNotification: UNNotification,
        withCompletionHandler: (UNNotificationPresentationOptions) -> Unit
    ) {
        withCompletionHandler(NOTIFICATION_PERMISSIONS)
    }
}

fun setupNotificationDelegate(notificationCenter: UNUserNotificationCenter) {
    if (notificationCenter.delegate == null) {
        println("setting up notification delegate on reminder setup")
        notificationCenter.delegate = Piley.notificationDelegate
    }
}