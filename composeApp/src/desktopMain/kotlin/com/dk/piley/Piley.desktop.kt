package com.dk.piley

import com.dk.piley.di.AppModule
import com.dk.piley.di.instantiateAppModule
import com.dk.piley.reminder.ReminderObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

actual class Piley {
    actual companion object {
        lateinit var appModule: AppModule
        lateinit var reminderObserver: ReminderObserver
        actual fun getModule(): AppModule = appModule
    }

    fun init() {
        appModule = instantiateAppModule()
        reminderObserver = ReminderObserver(
            appModule.notificationManager,
            appModule.taskRepository,
            appModule.pileRepository
        )
        // observe reminders every minute
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                println("Checking for reminders")
                reminderObserver.checkCurrentReminders()
                delay(1000 * 60)
            }
        }
    }
}