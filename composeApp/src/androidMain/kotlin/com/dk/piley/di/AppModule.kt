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
import com.dk.piley.model.pile.PileDao
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.task.TaskDao
import com.dk.piley.model.task.TaskRepository
import com.dk.piley.model.user.UserDao
import com.dk.piley.model.user.UserPrefsManager
import com.dk.piley.model.user.UserRepository
import com.dk.piley.reminder.NotificationManager
import com.dk.piley.reminder.ReminderActionHandler
import com.dk.piley.reminder.ReminderManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

interface AppModule {
    val pileDatabase: PileDatabase
    val userDatabase: UserDatabase
    val taskDao: TaskDao
    val taskRepository: TaskRepository
    val pileDao: PileDao
    val pileRepository: PileRepository
    val userDao: UserDao
    val userRepository: UserRepository
    val userPrefsManager: UserPrefsManager
    val reminderActionHandler: ReminderActionHandler
    val preferencesDataStore: DataStore<Preferences>
    val reminderManager: ReminderManager
    val notificationManager: NotificationManager
}

class AppModuleImpl(
    context: Context
) : AppModule {

    override val preferencesDataStore: DataStore<Preferences> by lazy {
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(SharedPreferencesMigration(context, USER_PREFERENCES)),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.preferencesDataStoreFile(USER_PREFERENCES) }
        )
    }

    override val pileDatabase: PileDatabase by lazy { PileDatabase.getInstance(context) }
    override val userDatabase: UserDatabase by lazy { UserDatabase.getInstance(context) }
    override val taskDao: TaskDao by lazy { pileDatabase.taskDao() }
    override val taskRepository: TaskRepository by lazy {
        TaskRepository(
            taskDao,
            reminderManager,
            notificationManager
        )
    }
    override val pileDao: PileDao by lazy { pileDatabase.pileDao() }
    override val pileRepository: PileRepository by lazy { PileRepository(pileDao) }
    override val userDao: UserDao by lazy { userDatabase.userDao() }
    override val userRepository: UserRepository by lazy {
        UserRepository(
            userDao,
            userPrefsManager
        )
    }
    override val userPrefsManager: UserPrefsManager by lazy { UserPrefsManager(preferencesDataStore) }
    override val reminderActionHandler: ReminderActionHandler by lazy {
        ReminderActionHandler(
            reminderManager,
            notificationManager,
            taskRepository,
            pileRepository,
            userRepository
        )
    }

    override val reminderManager: ReminderManager by lazy { ReminderManager(context) }
    override val notificationManager: NotificationManager by lazy { NotificationManager(context) }
}

private const val USER_PREFERENCES = "user_preferences"
