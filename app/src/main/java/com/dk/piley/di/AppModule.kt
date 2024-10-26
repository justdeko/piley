package com.dk.piley.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.dk.piley.model.PileDatabase
import com.dk.piley.model.UserDatabase
import com.dk.piley.model.pile.PileDao
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.task.TaskDao
import com.dk.piley.model.task.TaskRepository
import com.dk.piley.model.user.UserPrefsManager
import com.dk.piley.reminder.NotificationManager
import com.dk.piley.reminder.ReminderManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // database
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) =
        PileDatabase.getInstance(appContext)

    @Singleton
    @Provides
    fun provideUserDatabase(@ApplicationContext appContext: Context) =
        UserDatabase.getInstance(appContext)

    // task
    @Singleton
    @Provides
    fun provideTaskDao(db: PileDatabase) = db.taskDao()

    @Singleton
    @Provides
    fun provideTaskRepository(
        taskDao: TaskDao, reminderManager: ReminderManager, notificationManager: NotificationManager
    ) = TaskRepository(taskDao, reminderManager, notificationManager)

    // pile
    @Singleton
    @Provides
    fun providePileDao(db: PileDatabase) = db.pileDao()

    @Singleton
    @Provides
    fun providePileRepository(pileDao: PileDao) = PileRepository(pileDao)

    // user
    @Singleton
    @Provides
    fun provideUserDao(db: UserDatabase) = db.userDao()

    // user preferences manager
    @Singleton
    @Provides
    fun provideUserPrefsManager(
        prefsDatastore: DataStore<Preferences>
    ): UserPrefsManager {
        return UserPrefsManager(prefsDatastore)
    }
}
