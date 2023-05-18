package com.dk.piley.model.backup

import com.dk.piley.model.remote.Resource
import com.dk.piley.model.remote.backup.BackupApi
import com.dk.piley.model.remote.resourceSuccessfulFlow
import com.dk.piley.model.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class BackupRepository @Inject constructor(
    private val backupApi: BackupApi,
    private val userRepository: UserRepository
) {
    fun createOrUpdateBackup(email: String, file: File): Flow<Resource<String>> =
        resourceSuccessfulFlow {
            val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
            backupApi.createOrUpdateBackup(email, filePart, userRepository.localCredentials(email))
        }

    fun getBackupFile(email: String): Flow<Resource<File>> = flow {
        emit(Resource.Loading())
        val backupResponse = backupApi.getBackup(email, userRepository.localCredentials(email))
        try {
            if (backupResponse.isSuccessful) {
                val responseBody = backupResponse.body()
                if (responseBody != null) {
                    val file = File("path/to/save/file")
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
                    emit(Resource.Success(file))
                } else {
                    emit(Resource.Failure(Exception("Empty file body")))
                }
            } else {
                val body = backupResponse.errorBody()?.string() ?: "No error body"
                emit(Resource.Failure(Exception(body)))
            }
        } catch (e: IOException) {
            emit(Resource.Failure(e))
        }
    }.flowOn(Dispatchers.IO)

    fun deleteBackup(email: String): Flow<Resource<String>> = resourceSuccessfulFlow {
        backupApi.deleteBackup(email, userRepository.localCredentials(email))
    }

}