package com.dk.piley.util

// TODO fix this https://stackoverflow.com/a/73121886/4464680
actual fun getVersionName(): String {
    return try {
        val x = System.getProperty("jpackage.app-version")
        println(x)
        if (x == "null" || x == null) VERSION_NUMBER else x
    } catch (e: Exception) {
        VERSION_NUMBER
    }
}

const val VERSION_NUMBER = "0.7.0"