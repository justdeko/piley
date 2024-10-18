package com.dk.piley.model.remote.backup

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Streaming

const val BACKUP_RESOURCE_PREFIX = "backup"

/**
 * Interface to the backup endpoint to perform remote backup operations
 *
 */
interface BackupApi {
    @Multipart
    @POST("$BACKUP_RESOURCE_PREFIX/{email}")
    suspend fun createOrUpdateBackup(
        @Path("email") email: String,
        @Part filePart: MultipartBody.Part,
        @Header("Authorization") credentials: String
    ): Response<String>

    @GET("$BACKUP_RESOURCE_PREFIX/{email}")
    @Streaming
    suspend fun getBackup(
        @Path("email") email: String,
        @Header("Authorization") credentials: String
    ): Response<ResponseBody>

    @DELETE("$BACKUP_RESOURCE_PREFIX/{email}")
    suspend fun deleteBackup(
        @Path("email") email: String,
        @Header("Authorization") credentials: String
    ): Response<String>
}