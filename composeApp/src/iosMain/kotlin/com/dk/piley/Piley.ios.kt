package com.dk.piley

import com.dk.piley.di.AppModule
import com.dk.piley.di.instantiateAppModule
import com.dk.piley.reminder.NotificationActionHandler
import com.dk.piley.reminder.NotificationDelegate

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
    }
}