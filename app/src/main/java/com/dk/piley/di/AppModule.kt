package com.dk.piley.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.dk.piley.BuildConfig
import com.dk.piley.model.PileDatabase
import com.dk.piley.model.UserDatabase
import com.dk.piley.model.backup.BackupRepository
import com.dk.piley.model.pile.PileDao
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.remote.backup.BackupApi
import com.dk.piley.model.remote.user.UserApi
import com.dk.piley.model.task.TaskDao
import com.dk.piley.model.task.TaskRepository
import com.dk.piley.model.user.UserDao
import com.dk.piley.model.user.UserPrefsManager
import com.dk.piley.model.user.UserRepository
import com.dk.piley.reminder.NotificationManager
import com.dk.piley.reminder.ReminderManager
import com.dk.piley.util.BaseUrlInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
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

    // http url interceptor
    @Singleton
    @Provides
    fun provideBaseUrlInterceptor(userPrefsManager: UserPrefsManager) =
        BaseUrlInterceptor(userPrefsManager)

    @Singleton
    @Provides
    fun provideApi(baseUrlInterceptor: BaseUrlInterceptor): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.LOCAL_API_BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(baseUrlInterceptor)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .callTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .build()
        ).build()

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

    @Singleton
    @Provides
    fun provideUserApi(retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)

    @Singleton
    @Provides
    fun provideUserRepository(
        userDao: UserDao, userApi: UserApi, userPrefsManager: UserPrefsManager
    ) = UserRepository(userDao, userApi, userPrefsManager)

    // user preferences manager
    @Singleton
    @Provides
    fun provideUserPrefsManager(
        prefsDatastore: DataStore<Preferences>
    ): UserPrefsManager {
        return UserPrefsManager(prefsDatastore)
    }

    // backup
    @Singleton
    @Provides
    fun provideBackupApi(retrofit: Retrofit): BackupApi =
        retrofit.create(BackupApi::class.java)

    @Singleton
    @Provides
    fun provideBackupRepository(
        backupApi: BackupApi,
        userRepository: UserRepository
    ) = BackupRepository(backupApi, userRepository)

}
