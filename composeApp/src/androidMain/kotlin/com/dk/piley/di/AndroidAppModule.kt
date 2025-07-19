package com.dk.piley.di

import android.content.Context
import com.dk.piley.model.PileDatabase
import com.dk.piley.model.UserDatabase
import com.dk.piley.model.backup.DatabaseExporter
import com.dk.piley.model.backup.IDatabaseExporter
import com.dk.piley.model.calendar.AndroidReminderSyncManager
import com.dk.piley.model.calendar.TaskCalendarSyncManager
import com.dk.piley.model.getPileDatabase
import com.dk.piley.model.getUserDatabase
import com.dk.piley.model.sync.ISyncManager
import com.dk.piley.model.sync.SyncManager
import com.dk.piley.reminder.INotificationManager
import com.dk.piley.reminder.IReminderManager
import com.dk.piley.reminder.NotificationManager
import com.dk.piley.reminder.ReminderManager

fun instantiateAppModule(context: Context): AppModuleImpl {
    val preferencesDataStorePath =
        context.filesDir.resolve("datastore/$USER_PREFERENCES_PATH").absolutePath
    val pileDatabase: PileDatabase by lazy { getPileDatabase(context) }
    val userDatabase: UserDatabase by lazy { getUserDatabase(context) }
    val reminderManager: IReminderManager by lazy { ReminderManager(context) }
    val notificationManager: INotificationManager by lazy { NotificationManager(context) }
    val databaseExporter: IDatabaseExporter by lazy { DatabaseExporter(context) }
    val syncManager: ISyncManager by lazy { SyncManager(context) }
    val taskCalendarSyncManager: TaskCalendarSyncManager = AndroidReminderSyncManager(context)
    return AppModuleImpl(
        pileDatabase = pileDatabase,
        userDatabase = userDatabase,
        reminderManager = reminderManager,
        notificationManager = notificationManager,
        databaseExporter = databaseExporter,
        dataStorePath = preferencesDataStorePath,
        syncManager = syncManager,
        taskCalendarSyncManager = taskCalendarSyncManager,
    )
}