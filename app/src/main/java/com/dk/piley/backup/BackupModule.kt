package com.dk.piley.backup

import android.content.Context
import com.dk.piley.model.PileDatabase
import com.dk.piley.model.backup.BackupRepository
import com.dk.piley.model.user.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BackupModule {
    @Singleton
    @Provides
    fun provideBackupManager(
        backupRepository: BackupRepository,
        userRepository: UserRepository,
        db: PileDatabase,
        @ApplicationContext appContext: Context
    ) = BackupManager(backupRepository, userRepository, db, appContext)
}