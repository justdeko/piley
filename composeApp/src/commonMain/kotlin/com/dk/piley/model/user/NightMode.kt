package com.dk.piley.model.user

/**
 * Night mode enum representing possible night mode settings
 * SYSTEM: system settings determine whether the night theme is enabled
 * ENABLED: night theme is enabled
 * DISABLED: night theme is disabled
 *
 * @property value integer value of the night mode
 */
enum class NightMode(val value: Int) {
    SYSTEM(0),
    ENABLED(1),
    DISABLED(2);

    companion object {
        fun fromValue(value: Int) = entries.firstOrNull { it.value == value } ?: SYSTEM
    }
}

