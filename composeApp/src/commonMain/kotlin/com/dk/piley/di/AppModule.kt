package com.dk.piley.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.dk.piley.model.PileDatabase
import com.dk.piley.model.UserDatabase
import com.dk.piley.model.backup.IDatabaseExporter
import com.dk.piley.model.calendar.TaskCalendarSyncManager
import com.dk.piley.model.navigation.NavigationEventRepository
import com.dk.piley.model.navigation.ShortcutEventRepository
import com.dk.piley.model.notification.NotificationRepository
import com.dk.piley.model.pile.PileDao
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.sync.ISyncManager
import com.dk.piley.model.sync.SyncCoordinator
import com.dk.piley.model.task.TaskDao
import com.dk.piley.model.task.TaskRepository
import com.dk.piley.model.user.UserDao
import com.dk.piley.model.user.UserPrefsManager
import com.dk.piley.model.user.UserRepository
import com.dk.piley.reminder.INotificationManager
import com.dk.piley.reminder.IReminderActionHandler
import com.dk.piley.reminder.IReminderManager
import com.dk.piley.reminder.ReminderActionHandler
import okio.Path.Companion.toPath

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
    val reminderActionHandler: IReminderActionHandler
    val preferencesDataStore: DataStore<Preferences>
    val reminderManager: IReminderManager
    val notificationManager: INotificationManager
    val notificationRepository: NotificationRepository
    val navigationEventRepository: NavigationEventRepository
    val shortcutEventRepository: ShortcutEventRepository
    val databaseExporter: IDatabaseExporter
    val syncManager: ISyncManager
    val syncCoordinator: SyncCoordinator
    val taskCalendarSyncManager: TaskCalendarSyncManager
}

class AppModuleImpl(
    override val pileDatabase: PileDatabase,
    override val userDatabase: UserDatabase,
    override val reminderManager: IReminderManager,
    override val notificationManager: INotificationManager,
    override val databaseExporter: IDatabaseExporter,
    override val syncManager: ISyncManager,
    override val taskCalendarSyncManager: TaskCalendarSyncManager,
    dataStorePath: String,
) : AppModule {

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
    override val reminderActionHandler: IReminderActionHandler by lazy {
        ReminderActionHandler(
            reminderManager,
            notificationManager,
            taskRepository,
            pileRepository,
            userRepository
        )
    }
    override val preferencesDataStore: DataStore<Preferences> by lazy {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { dataStorePath.toPath() }
        )
    }

    override val notificationRepository: NotificationRepository by lazy { NotificationRepository() }
    override val navigationEventRepository: NavigationEventRepository by lazy { NavigationEventRepository() }
    override val shortcutEventRepository: ShortcutEventRepository by lazy { ShortcutEventRepository() }
    override val syncCoordinator: SyncCoordinator by lazy {
        SyncCoordinator(
            syncManager,
            userPrefsManager
        )
    }
}

internal const val USER_PREFERENCES_PATH = "user_preferences.preferences_pb"
