package com.dk.piley.util

import com.dk.piley.Piley

actual fun getVersionName(): String {
    return try {
        val context = Piley.application
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "Unknown"
    } catch (e: Exception) {
        "Unknown"
    }
}
actual val appPlatform: Platform = Platform.ANDROID