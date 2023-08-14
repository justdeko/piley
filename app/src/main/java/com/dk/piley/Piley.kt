package com.dk.piley

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber.DebugTree
import timber.log.Timber.Forest.plant


@HiltAndroidApp
class Piley : Application() {
    override fun onCreate() {
        super.onCreate()
        // init timber
        if (BuildConfig.DEBUG) {
            plant(DebugTree())
        } else {
            // TODO plant crash reporting
            plant(DebugTree())
        }
    }
}