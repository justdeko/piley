package com.dk.piley.model.remote.user

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

const val USER_RESOURCE_PREFIX = "users"

interface UserApi {
    @POST(USER_RESOURCE_PREFIX)
    suspend fun createUser(@Body userRequest: UserRequest): Response<String>

    @PUT(USER_RESOURCE_PREFIX)
    suspend fun updateUser(
        @Body userUpdateRequest: UserUpdateRequest,
        @Header("Authorization") credentials: String
    ): Response<String>

    @DELETE("$USER_RESOURCE_PREFIX/{email}")
    suspend fun deleteUser(
        @Path("email") email: String,
        @Header("Authorization") credentials: String
    ): Response<String>

    @GET("$USER_RESOURCE_PREFIX/{email}")
    suspend fun getUser(
        @Path("email") email: String,
        @Header("Authorization") credentials: String
    ): Response<UserResponse>
}