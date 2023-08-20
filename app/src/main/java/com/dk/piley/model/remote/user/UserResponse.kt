package com.dk.piley.model.remote.user

/**
 * User response returned by the api endpoint
 *
 * @property email user email
 * @property name user name
 */
data class UserResponse(
    val email: String,
    val name: String,
)
