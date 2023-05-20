package com.dk.piley.backup

import android.content.Context
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
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.last
import org.threeten.bp.Instant
import timber.log.Timber
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class BackupManager @Inject constructor(
    private val backupRepository: BackupRepository,
    private val userRepository: UserRepository,
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

    suspend fun doBackup(): Boolean {
        // attempt to push
        when (pushBackupToRemoteForUserFlow().last()) {
            is Resource.Loading -> Timber.i("Syncing local backup to remote")
            is Resource.Success -> return true
                .also { Timber.i("Backup successfully synced") }

            is Resource.Failure -> return false.also { Timber.i("Failed to sync backup") }
        }
        return false.also { Timber.i("Failed to sync backup") }
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

