package com.dk.piley

import android.app.Application
import com.dk.piley.di.AppModule
import com.dk.piley.di.instantiateAppModule


class Piley : Application() {
    companion object {
        lateinit var appModule: AppModule
        lateinit var application: Application
    }

    override fun onCreate() {
        super.onCreate()
        appModule = instantiateAppModule(this)
        application = this
    }
}