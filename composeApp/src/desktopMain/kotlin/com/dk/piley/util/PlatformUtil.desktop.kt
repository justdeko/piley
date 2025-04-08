package com.dk.piley.util

import java.io.File

// only works on distributable https://stackoverflow.com/a/73121886/4464680
actual fun getVersionName(): String {
    return try {
        val x = System.getProperty("jpackage.app-version")
        if (x == "null" || x == null) VERSION_NUMBER else x
    } catch (e: Exception) {
        VERSION_NUMBER
    }
}

fun resourcesPath(): String? {
    return runCatching {
        val resourcesDirectory = File(System.getProperty("compose.application.resources.dir"))
        return resourcesDirectory.canonicalPath
    }.getOrNull()
}

const val VERSION_NUMBER = "0.8.5"

actual val appPlatform: Platform = Platform.DESKTOP