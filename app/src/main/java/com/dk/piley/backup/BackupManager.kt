package com.dk.piley.backup

import android.content.Context
import com.dk.piley.model.DATABASE_NAME
import com.dk.piley.model.PileDatabase
import com.dk.piley.model.backup.BackupRepository
import com.dk.piley.model.common.Resource
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.remote.backup.FileResponse
import com.dk.piley.model.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.last
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import timber.log.Timber
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

/**
 * Handles local and remote backup operations
 * like downloading backups, overwriting the database, pushing the current database to remote, etc.
 *
 * @property backupRepository instance of backup repository for remote calls
 * @property userRepository instance of user repository to get user and user preferences regarding backup
 * @property db instance of the database to close when needed
 * @property context generic context
 */
class BackupManager @Inject constructor(
    private val backupRepository: BackupRepository,
    private val userRepository: UserRepository,
    private val pileRepository: PileRepository,
    private val db: PileDatabase,
    private val context: Context
) {
    /**
     * Flow for syncing the remote backup from the server for the current user.
     *
     * @return Resource Flow with a nullable [Instant]
     * that represents whether the sync was needed (null if not) and when the downloaded backup occurred
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun syncBackupToLocalForUserFlow(): Flow<Resource<Instant?>> {
        val user = userRepository.getSignedInUserEntity() ?: return flowOf(
            Resource.Failure(
                Exception("no user found")
            )
        )
        // if user is in offline mode, do nothing and return successful flow
        if (user.isOffline) {
            return flowOf(Resource.Success(null))
        }
        // if backup fetch wasn't longer than x days ago, do nothing and return successful flow
        val backupPullDuration = user.loadBackupAfterDays
        if (backupPullDuration > 0
            && user.lastBackupQuery != null
            && user.lastBackupQuery > (Clock.System.now().minus(
                backupPullDuration,
                DateTimeUnit.DAY,
                TimeZone.currentSystemDefault()
            ))
        ) {
            Timber.i("Last backup not too long ago, aborting backup sync")
            return flowOf(Resource.Success(null))
        }
        return backupRepository.getBackupFileFlow(
            user.email, context
        ).flatMapConcat {
            when (it) {
                is Resource.Loading -> flowOf(Resource.Loading())
                is Resource.Success -> createOrUpdateDbFileFlow(it.data).also {
                    userRepository.insertUser(
                        user.copy(lastBackupQuery = Clock.System.now())
                    )
                }

                is Resource.Failure -> flowOf(Resource.Failure(it.exception))
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * Flow for uploading the local backup file to the server
     *
     * @return Resource flow with the response string if successful
     */
    suspend fun pushBackupToRemoteForUserFlow(): Flow<Resource<String>> {
        val email = userRepository.getSignedInUserEmail()
        return if (email.isNotBlank()) {
            // perform wal checkpoint to copy data over to main db file
            pileRepository.performDatabaseCheckpoint()
            // push db file to remote
            val dbPath = context.getDatabasePath(DATABASE_NAME)
            Timber.i("Pushing backup from file $dbPath with size ${dbPath.length()}b")
            backupRepository.createOrUpdateBackupFlow(email, dbPath).flowOn(Dispatchers.IO)
        } else {
            emptyFlow()
        }
    }

    /**
     * Performs a [doBackup] call if the last backup date was outside the backup frequency set by the user
     *
     */
    suspend fun performBackupIfNecessary() {
        userRepository.getSignedInUserEntity()?.let {
            // if user is offline, don't perform backup
            if (it.isOffline) return
            // get date of last backup and perform new backup if date was too long ago
            val lastBackup = it.lastBackup
            val latestBackupDate =
                Clock.System.now()
                    .minus(
                        it.defaultBackupFrequency.toLong(),
                        DateTimeUnit.DAY,
                        TimeZone.currentSystemDefault()
                    )
            if (lastBackup != null && lastBackup < latestBackupDate) {
                doBackup()
            }
        }
    }

    /**
     * Performs a backup by attempting to upload the current db file to the server
     *
     * @return Boolean whether the backup was successful
     */
    suspend fun doBackup(): Boolean {
        // attempt to push
        when (pushBackupToRemoteForUserFlow().last()) {
            is Resource.Loading -> Timber.i("Syncing local backup to remote")
            is Resource.Success -> {
                val user = userRepository.getSignedInUserEntity()
                user?.let { userRepository.insertUser(user.copy(lastBackup = Clock.System.now())) }
                Timber.i("Backup successfully synced")
                return true
            }

            is Resource.Failure -> return false.also { Timber.i("Failed to sync backup") }
        }
        return false.also { Timber.i("Failed to sync backup") }
    }

    /**
     * Flow of updating the current database if the received user backup is more recent than the current db
     *
     * @param fileResponse the file response from the server containing the backup file and metadata
     * @return a resource flow with an Instant which is null if the local database was not overwritten
     */
    private fun createOrUpdateDbFileFlow(fileResponse: FileResponse): Flow<Resource<Instant?>> =
        flow {
            emit(Resource.Loading())
            val dbFile = context.getDatabasePath(DATABASE_NAME)
            val localLastModified = Instant.fromEpochMilliseconds(dbFile.lastModified())
            // if local file was modified more recently than remote file
            // then emit success but with false representing no update of local backup
            if (fileResponse.lastModified < localLastModified) {
                Timber.i("Last modification date of local file: $localLastModified")
                Timber.i("Last modification date of local file: ${fileResponse.lastModified}")
                Timber.i("Remote backup file is outdated, no overwrite needed")
                emit(Resource.Success(null))
                return@flow
            }
            val dbPath = dbFile.absolutePath
            // close db instance
            db.close()
            // delete old file
            if (dbFile.exists()) {
                Timber.i("Deleting backup file at $dbPath")
                dbFile.delete()
            }
            // attempt to copy new file
            try {
                Timber.i("Writing backup file at $dbPath")
                val inputStream = FileInputStream(fileResponse.file)
                val outputStream = FileOutputStream(dbPath)
                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                // delete temp file
                Timber.i("Deleting temp file at ${fileResponse.file.absolutePath}")
                fileResponse.file.delete()
                emit(Resource.Success(fileResponse.lastModified))
            } catch (ex: IOException) {
                emit(Resource.Failure(ex))
            }
        }.flowOn(Dispatchers.IO)
}

