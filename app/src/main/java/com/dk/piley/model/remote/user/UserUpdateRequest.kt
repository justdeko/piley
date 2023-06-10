package com.dk.piley.model.remote.user

data class UserUpdateRequest(
    val email: String,
    val name: String,
    val oldPassword: String,
    val newPassword: String,
)
