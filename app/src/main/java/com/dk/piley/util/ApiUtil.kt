package com.dk.piley.util

import okhttp3.Credentials

fun credentials(username: String, password: String) = Credentials.basic(username, password)
