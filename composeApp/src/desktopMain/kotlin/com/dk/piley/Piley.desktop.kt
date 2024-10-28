package com.dk.piley

import com.dk.piley.di.AppModule
import com.dk.piley.di.instantiateAppModule

actual class Piley {
    actual companion object {
        lateinit var appModule: AppModule
        actual fun getModule(): AppModule = appModule
    }

    fun init() {
        appModule = instantiateAppModule()
    }
}