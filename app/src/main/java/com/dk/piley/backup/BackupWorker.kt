package com.dk.piley.backup

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dk.piley.model.common.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.last
import timber.log.Timber

@HiltWorker
class BackupWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val backupManager: BackupManager
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        // attempt to push
        when (backupManager.pushBackupToRemoteForUserFlow().last()) {
            is Resource.Loading -> Timber.i("Syncing local backup to remote")
            is Resource.Success -> return Result.success()
                .also { Timber.i("Backup successfully synced") }

            is Resource.Failure -> return Result.failure()
                .also { Timber.i("Failed to sync backup") }
        }
        return Result.failure()
    }
}