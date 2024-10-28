package com.dk.piley

import android.app.Application
import com.dk.piley.di.AppModule
import com.dk.piley.di.instantiateAppModule


actual class Piley : Application() {
    actual companion object {
        lateinit var appModule: AppModule
        lateinit var application: Application
        actual fun getModule(): AppModule = appModule
    }

    override fun onCreate() {
        super.onCreate()
        appModule = instantiateAppModule(this)
        application = this
    }
}