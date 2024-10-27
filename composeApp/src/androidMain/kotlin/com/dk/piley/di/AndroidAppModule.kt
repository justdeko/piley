package com.dk.piley.di

import android.content.Context
import com.dk.piley.model.PileDatabase
import com.dk.piley.model.UserDatabase
import com.dk.piley.reminder.INotificationManager
import com.dk.piley.reminder.IReminderManager
import com.dk.piley.reminder.NotificationManager
import com.dk.piley.reminder.ReminderManager

fun instantiateAppModule(context: Context): AppModuleImpl {
    val preferencesDataStorePath = context.filesDir.resolve("datastore/$USER_PREFERENCES_PATH").absolutePath
    val pileDatabase: PileDatabase by lazy { PileDatabase.getInstance(context) }
    val userDatabase: UserDatabase by lazy { UserDatabase.getInstance(context) }
    val reminderManager: IReminderManager by lazy { ReminderManager(context) }
    val notificationManager: INotificationManager by lazy { NotificationManager(context) }
    return AppModuleImpl(
        pileDatabase,
        userDatabase,
        reminderManager,
        notificationManager,
        preferencesDataStorePath
    )
}