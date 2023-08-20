package com.dk.piley.model.remote.user

/**
 * Entity representing a user request to the user api endpoint
 *
 * @property email user email
 * @property name user name
 * @property password user password
 */
data class UserRequest(
    val email: String,
    val name: String,
    val password: String
)
