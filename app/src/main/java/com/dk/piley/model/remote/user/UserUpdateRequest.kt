package com.dk.piley.model.remote.user

data class UserUpdateRequest(
    val oldEmail: String,
    val newEmail: String,
    val name: String,
    val oldPassword: String,
    val newPassword: String,
)
