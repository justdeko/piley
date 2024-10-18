package com.dk.piley.model.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import timber.log.Timber

/**
 * Resource representing anything with loading state resulting either in success or failure
 *
 * @param T generic data type which is returned on success
 */
sealed class Resource<out T> {
    class Loading<out T> : Resource<T>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Failure<out T>(val exception: Exception) : Resource<T>()
}

/**
 * Represents a resource flow for remote calls. Starts with emitting a loading resource
 *
 * @param T a generic resource result type
 * @param apiRequest suspend function that performs an api request and returns a response with the generic type
 * @return a resource flow returning resulting in a success with the generic type or failure
 */
fun <T> resourceSuccessfulFlow(apiRequest: suspend () -> Response<T>): Flow<Resource<T>> = flow {
    emit(Resource.Loading())
    try {
        val response = apiRequest()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                emit(Resource.Success(body))
            } else {
                emit(Resource.Failure(Exception("empty body")))
            }
        } else {
            Timber.e(response.message())
            emit(Resource.Failure(Exception(response.message())))
        }
    } catch (e: Exception) {
        Timber.e(e)
        emit(Resource.Failure(e))
    }
}.flowOn(Dispatchers.IO)
