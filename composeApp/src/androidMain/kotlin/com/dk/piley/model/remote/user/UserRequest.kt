package com.dk.piley.model.remote.user

import com.google.gson.annotations.SerializedName

/**
 * Entity representing a user request to the user api endpoint
 *
 * @property email user email
 * @property name user name
 * @property password user password
 */
data class UserRequest(
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("password") val password: String
)
