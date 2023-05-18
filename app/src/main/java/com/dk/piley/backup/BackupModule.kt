package com.dk.piley.backup

import android.content.Context
import com.dk.piley.model.PileDatabase
import com.dk.piley.model.backup.BackupRepository
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BackupModule {
    fun provideBackupManager(
        backupRepository: BackupRepository,
        db: PileDatabase,
        @ApplicationContext appContext: Context
    ) = BackupManager(backupRepository, db, appContext)
}