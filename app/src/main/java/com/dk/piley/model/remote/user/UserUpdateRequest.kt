package com.dk.piley.model.remote.user

import com.google.gson.annotations.SerializedName

/**
 * User update request sent to the api endpoint
 *
 * @property email user email
 * @property name user password
 * @property oldPassword old password of the user
 * @property newPassword new password of the user
 */
data class UserUpdateRequest(
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("oldPassword") val oldPassword: String,
    @SerializedName("newPassword") val newPassword: String,
)
