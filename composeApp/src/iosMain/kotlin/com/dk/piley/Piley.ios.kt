package com.dk.piley

import com.dk.piley.di.AppModule
import com.dk.piley.di.instantiateAppModule
import com.dk.piley.reminder.NotificationActionHandler
import com.dk.piley.reminder.NotificationDelegate
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNUserNotificationCenter

actual class Piley {
    actual companion object {
        lateinit var appModule: AppModule
        lateinit var notificationActionHandler: NotificationActionHandler
        lateinit var notificationDelegate: NotificationDelegate
        actual fun getModule(): AppModule = appModule
    }

    fun init() {
        appModule = instantiateAppModule()
        notificationActionHandler = NotificationActionHandler()
        notificationDelegate = NotificationDelegate()

        // Check if notifications are authorized and set up the delegate
        val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
        notificationCenter.getNotificationSettingsWithCompletionHandler {
            if (it?.authorizationStatus == UNAuthorizationStatusAuthorized) {
                if (notificationCenter.delegate == null) {
                    println("setting up notification delegate on init")
                    notificationCenter.delegate = notificationDelegate
                }
            }
        }
    }
}