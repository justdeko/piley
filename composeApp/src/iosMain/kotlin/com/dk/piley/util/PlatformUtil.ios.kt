package com.dk.piley.util

actual fun getVersionName(): String =
    platform.Foundation.NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as String

actual val appPlatform: Platform = Platform.IOS