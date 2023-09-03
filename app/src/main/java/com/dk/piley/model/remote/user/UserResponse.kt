package com.dk.piley.model.remote.user

import com.google.gson.annotations.SerializedName

/**
 * User response returned by the api endpoint
 *
 * @property email user email
 * @property name user name
 */
data class UserResponse(
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
)
