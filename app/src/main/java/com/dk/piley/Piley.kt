package com.dk.piley

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Piley : Application() {
    override fun onCreate() {
        super.onCreate()
        // date and time api init
        AndroidThreeTen.init(this)
    }
}