package com.dk.piley.util

expect fun getVersionName(): String

expect val appPlatform: Platform

enum class Platform {
    ANDROID,
    IOS,
    DESKTOP;

    companion object {
        fun fromValue(value: String): Platform {
            return when (value.uppercase()) {
                "ANDROID" -> ANDROID
                "IOS" -> IOS
                "DESKTOP" -> DESKTOP
                else -> DESKTOP
            }
        }
    }
}