package com.dk.piley.di

import com.dk.piley.model.PileDatabase
import com.dk.piley.model.UserDatabase
import com.dk.piley.model.getPileDatabase
import com.dk.piley.model.getUserDatabase
import com.dk.piley.reminder.INotificationManager
import com.dk.piley.reminder.IReminderManager
import com.dk.piley.reminder.NotificationManager
import com.dk.piley.reminder.ReminderManager
import java.io.File

fun instantiateAppModule(): AppModuleImpl {
    val preferencesDataStorePath =
        System.getProperty("java.io.tmpdir") + File.separator + USER_PREFERENCES_PATH
    val pileDatabase: PileDatabase by lazy { getPileDatabase() }
    val userDatabase: UserDatabase by lazy { getUserDatabase() }
    val reminderManager: IReminderManager by lazy { ReminderManager() }
    val notificationManager: INotificationManager by lazy { NotificationManager() }
    return AppModuleImpl(
        pileDatabase,
        userDatabase,
        reminderManager,
        notificationManager,
        preferencesDataStorePath
    )
}