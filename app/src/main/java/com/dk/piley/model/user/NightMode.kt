package com.dk.piley.model.user

enum class NightMode(val value: Int) {
    SYSTEM(0),
    ENABLED(1),
    DISABLED(2);

    companion object {
        fun fromValue(value: Int) = values().firstOrNull { it.value == value } ?: SYSTEM
    }
}

