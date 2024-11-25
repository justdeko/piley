package com.dk.piley.util

expect fun getVersionName(): String

expect val appPlatform: Platform

enum class Platform {
    ANDROID,
    IOS,
    DESKTOP,
}