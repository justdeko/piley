package com.dk.piley.di

import com.dk.piley.model.PileDatabase
import com.dk.piley.model.UserDatabase
import com.dk.piley.model.backup.DatabaseExporter
import com.dk.piley.model.backup.IDatabaseExporter
import com.dk.piley.model.getPileDatabase
import com.dk.piley.model.getUserDatabase
import com.dk.piley.model.sync.ISyncManager
import com.dk.piley.model.sync.SyncManager
import com.dk.piley.reminder.INotificationManager
import com.dk.piley.reminder.IReminderManager
import com.dk.piley.reminder.NotificationManager
import com.dk.piley.reminder.ReminderManager
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
fun instantiateAppModule(): AppModuleImpl {
    val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )

    val preferencesDataStorePath =
        requireNotNull(documentDirectory).path + "/" + USER_PREFERENCES_PATH
    val pileDatabase: PileDatabase by lazy { getPileDatabase() }
    val userDatabase: UserDatabase by lazy { getUserDatabase() }
    val reminderManager: IReminderManager by lazy { ReminderManager() }
    val notificationManager: INotificationManager by lazy { NotificationManager() }
    val databaseExporter: IDatabaseExporter by lazy { DatabaseExporter() }
    val syncManager: ISyncManager by lazy { SyncManager() }
    return AppModuleImpl(
        pileDatabase = pileDatabase,
        userDatabase = userDatabase,
        reminderManager = reminderManager,
        notificationManager = notificationManager,
        databaseExporter = databaseExporter,
        dataStorePath = preferencesDataStorePath,
        syncManager = syncManager
    )
}