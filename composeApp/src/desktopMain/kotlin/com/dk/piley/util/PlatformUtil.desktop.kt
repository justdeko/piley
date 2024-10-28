package com.dk.piley.util

actual fun getVersionName(): String = System.getProperty("jpackage.app-version")