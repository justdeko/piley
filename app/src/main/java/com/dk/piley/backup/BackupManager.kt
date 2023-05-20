package com.dk.piley.backup

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dk.piley.model.DATABASE_NAME
import com.dk.piley.model.PileDatabase
import com.dk.piley.model.backup.BackupRepository
import com.dk.piley.model.common.Resource
import com.dk.piley.model.remote.backup.FileResponse
import com.dk.piley.model.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import org.threeten.bp.Instant
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val BACKUP_WORKER_TAG = "backup"

class BackupManager @Inject constructor(
    private val backupRepository: BackupRepository,
    private val userRepository: UserRepository,
    private val workManager: WorkManager,
    private val db: PileDatabase,
    private val context: Context
) {
    @OptIn(FlowPreview::class)
    suspend fun syncBackupToLocalForUserFlow(): Flow<Resource<Boolean>> =
        backupRepository.getBackupFileFlow(
            userRepository.getSignedInUserEmail(), context
        ).flatMapConcat {
            when (it) {
                is Resource.Loading -> flowOf(Resource.Loading())
                is Resource.Success -> createOrUpdateDbFileFlow(it.data)
                is Resource.Failure -> flowOf(Resource.Failure(it.exception))
            }
        }.flowOn(Dispatchers.IO)

    suspend fun pushBackupToRemoteForUserFlow(): Flow<Resource<String>> {
        val email = userRepository.getSignedInUserEmail()
        return if (email.isNotBlank()) {
            backupRepository.createOrUpdateBackupFlow(
                email,
                context.getDatabasePath(DATABASE_NAME)
            ).flowOn(Dispatchers.IO)
        } else {
            emptyFlow()
        }
    }


    suspend fun schedulePeriodicBackup() {
        val frequencyInSeconds =
            userRepository.getSignedInUserNotNull().firstOrNull()?.defaultBackupFrequency?.toLong()
                ?: 60
        val request =
            PeriodicWorkRequestBuilder<BackupWorker>(frequencyInSeconds, TimeUnit.SECONDS)
                .build()
        workManager.enqueueUniquePeriodicWork(
            BACKUP_WORKER_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun cancelPeriodicBackup() {
        workManager.cancelUniqueWork(BACKUP_WORKER_TAG)
    }


    private fun createOrUpdateDbFileFlow(fileResponse: FileResponse): Flow<Resource<Boolean>> =
        flow {
            emit(Resource.Loading())
            val dbFile = context.getDatabasePath(DATABASE_NAME)
            val localLastModified = Instant.ofEpochMilli(dbFile.lastModified())
            // if local file was modified more recently than remote file
            // then emit success but with false representing no update of local backup
            if (fileResponse.lastModified < localLastModified) {
                emit(Resource.Success(false))
                return@flow
            }
            val dbPath = dbFile.absolutePath
            // close db instance
            db.close()
            // delete old file
            if (dbFile.exists()) {
                dbFile.delete()
            }
            // attempt to copy new file
            try {
                val inputStream = FileInputStream(fileResponse.file)
                val outputStream = FileOutputStream(dbPath)
                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                // delete temp file
                fileResponse.file.delete()
                emit(Resource.Success(true))
            } catch (ex: IOException) {
                emit(Resource.Failure(ex))
            }
        }.flowOn(Dispatchers.IO)
}

