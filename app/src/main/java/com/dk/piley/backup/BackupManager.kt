package com.dk.piley.backup

import android.content.Context
import android.util.Log
import com.dk.piley.model.DATABASE_NAME_WITH_EXTENSION
import com.dk.piley.model.PileDatabase
import com.dk.piley.model.backup.BackupRepository
import com.dk.piley.model.common.Resource
import com.dk.piley.model.remote.backup.FileResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class BackupManager @Inject constructor(
    private val backupRepository: BackupRepository,
    private val db: PileDatabase,
    private val context: Context
) {
    @OptIn(FlowPreview::class)
    fun syncBackupToLocalForUserFlow(email: String): Flow<Resource<Boolean>> =
        backupRepository.getBackupFileFlow(email).flatMapConcat {
            when (it) {
                is Resource.Loading -> flowOf(Resource.Loading())
                is Resource.Success -> createOrUpdateDbFileFlow(it.data)
                is Resource.Failure -> flowOf(Resource.Failure(it.exception))
            }
        }

    fun pushBackupToRemoteForUserFlow(email: String): Flow<Resource<String>> =
        backupRepository.createOrUpdateBackupFlow(
            email,
            context.getDatabasePath(DATABASE_NAME_WITH_EXTENSION)
        )

    private fun createOrUpdateDbFileFlow(fileResponse: FileResponse): Flow<Resource<Boolean>> =
        flow {
            emit(Resource.Loading())
            val dbFile = context.getDatabasePath(DATABASE_NAME_WITH_EXTENSION)
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
                emit(Resource.Success(true))
            } catch (ex: IOException) {
                Log.e("BackupManager", ex.toString())
                emit(Resource.Failure(ex))
            }
        }.flowOn(Dispatchers.IO)
}

