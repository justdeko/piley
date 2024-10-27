package com.dk.piley.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.dk.piley.model.PileDatabase
import com.dk.piley.model.UserDatabase
import com.dk.piley.reminder.INotificationManager
import com.dk.piley.reminder.IReminderManager
import com.dk.piley.reminder.NotificationManager
import com.dk.piley.reminder.ReminderManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

fun instantiateAppModule(context: Context): AppModuleImpl {
    val preferencesDataStore: DataStore<Preferences> by lazy {
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(SharedPreferencesMigration(context, USER_PREFERENCES)),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.preferencesDataStoreFile(USER_PREFERENCES) }
        )
    }
    val pileDatabase: PileDatabase by lazy { PileDatabase.getInstance(context) }
    val userDatabase: UserDatabase by lazy { UserDatabase.getInstance(context) }
    val reminderManager: IReminderManager by lazy { ReminderManager(context) }
    val notificationManager: INotificationManager by lazy { NotificationManager(context) }
    return AppModuleImpl(
        pileDatabase,
        userDatabase,
        preferencesDataStore,
        reminderManager,
        notificationManager
    )
}