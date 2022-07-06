package com.dk.piley.di

import android.content.Context
import com.dk.piley.model.PileDatabase
import com.dk.piley.model.task.TaskDao
import com.dk.piley.model.task.TaskRepository
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

    // task
    @Singleton
    @Provides
    fun provideTaskDao(db: PileDatabase) = db.taskDao()

    @Singleton
    @Provides
    fun provideRepository(taskDao: TaskDao) = TaskRepository(taskDao)
}