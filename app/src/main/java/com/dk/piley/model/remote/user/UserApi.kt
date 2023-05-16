package com.dk.piley.model.remote.user

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {
    @POST("user")
    suspend fun createUser(@Body userRequest: UserRequest): Response<String>

    @PUT("user")
    suspend fun updateUser(
        @Body userUpdateRequest: UserUpdateRequest,
        @Header("Authorization") credentials: String
    ): Response<String>

    @DELETE("user/{email}")
    suspend fun deleteUser(
        @Path("email") email: String,
        @Header("Authorization") credentials: String
    ): Response<String>

    @GET("/authenticate")
    suspend fun authenticateUser(
        @Header("Authorization") credentials: String
    ): Response<String>
}