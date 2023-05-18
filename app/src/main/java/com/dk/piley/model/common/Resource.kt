package com.dk.piley.model.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

sealed class Resource<out T> {
    class Loading<out T> : Resource<T>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Failure<out T>(val exception: Exception) : Resource<T>()
}

fun <T> resourceSuccessfulFlow(apiRequest: suspend () -> Response<T>): Flow<Resource<T>> = flow {
    emit(Resource.Loading())
    val response = apiRequest()
    if (response.isSuccessful) {
        val body = response.body()
        if (body != null) {
            emit(Resource.Success(body))
        } else {
            emit(Resource.Failure(Exception("empty body")))
        }
    } else {
        val body = response.errorBody()?.string() ?: "No error body"
        emit(Resource.Failure(Exception(body)))
    }
}.flowOn(Dispatchers.IO)
