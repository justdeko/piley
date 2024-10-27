package com.dk.piley

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform