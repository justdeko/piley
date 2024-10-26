package com.dk.piley

import android.app.Application
import com.dk.piley.di.AppModule
import com.dk.piley.di.AppModuleImpl


class Piley : Application() {
    companion object {
        lateinit var appModule: AppModule
        lateinit var application: Application
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl(this)
        application = this
    }
}