package com.dk.piley

import com.dk.piley.di.AppModule

expect class Piley {
    companion object {
        fun getModule(): AppModule
    }
}