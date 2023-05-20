package com.dk.piley

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber.DebugTree
import timber.log.Timber.Forest.plant
import javax.inject.Inject


@HiltAndroidApp
class Piley : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        // date and time api init
        AndroidThreeTen.init(this)
        // init timber
        if (BuildConfig.DEBUG) {
            plant(DebugTree())
        } else {
            // TODO plant crash reporting
            plant(DebugTree())
        }
    }
}