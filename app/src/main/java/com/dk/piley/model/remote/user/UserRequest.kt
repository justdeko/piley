package com.dk.piley.model.remote.user

data class UserRequest(
    val email: String,
    val name: String,
    val password: String
)
