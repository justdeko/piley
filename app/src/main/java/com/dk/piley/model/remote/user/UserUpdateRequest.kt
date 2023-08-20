package com.dk.piley.model.remote.user

/**
 * User update request sent to the api endpoint
 *
 * @property email user email
 * @property name user password
 * @property oldPassword old password of the user
 * @property newPassword new password of the user
 */
data class UserUpdateRequest(
    val email: String,
    val name: String,
    val oldPassword: String,
    val newPassword: String,
)
