package com.dk.piley.model.backup

import android.content.Context
import com.dk.piley.model.common.Resource
import com.dk.piley.model.common.resourceSuccessfulFlow
import com.dk.piley.model.remote.backup.BackupApi
import com.dk.piley.model.remote.backup.FileResponse
import com.dk.piley.model.user.UserRepository
import com.dk.piley.util.contentDispositionHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.threeten.bp.Instant
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class BackupRepository @Inject constructor(
    private val backupApi: BackupApi,
    private val userRepository: UserRepository,
) {
    fun createOrUpdateBackupFlow(email: String, file: File): Flow<Resource<String>> =
        resourceSuccessfulFlow {
            val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
            backupApi.createOrUpdateBackup(email, filePart, userRepository.localCredentials(email))
        }

    fun getBackupFileFlow(email: String, context: Context): Flow<Resource<FileResponse>> = flow {
        emit(Resource.Loading())
        try {
            val backupResponse = backupApi.getBackup(email, userRepository.localCredentials(email))
            if (backupResponse.isSuccessful) {
                val responseBody = backupResponse.body()
                val contentDispositionHeaders = backupResponse.headers().contentDispositionHeaders()
                val filename = contentDispositionHeaders?.filename ?: "backup.db"
                val lastModified = contentDispositionHeaders?.lastModified ?: Instant.MIN
                if (responseBody != null) {
                    val file = File(context.cacheDir, filename)
                    val inputStream = responseBody.byteStream()
                    val outputStream = FileOutputStream(file)
                    val buffer = ByteArray(4096)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    outputStream.flush()
                    outputStream.close()
                    inputStream.close()
                    emit(Resource.Success(FileResponse(file, lastModified)))
                } else {
                    emit(Resource.Failure(Exception("Empty file body")))
                }
            } else {
                val body = backupResponse.errorBody()?.string() ?: "No error body"
                emit(Resource.Failure(Exception(body)))
            }
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }.flowOn(Dispatchers.IO)

    fun deleteBackupFlow(email: String): Flow<Resource<String>> = resourceSuccessfulFlow {
        backupApi.deleteBackup(email, userRepository.localCredentials(email))
    }

}