package com.dk.piley.util

import com.dk.piley.BuildConfig

actual fun getVersionName(): String = BuildConfig.VERSION_NAME
actual val appPlatform: Platform = Platform.ANDROID