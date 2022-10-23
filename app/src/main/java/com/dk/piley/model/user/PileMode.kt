package com.dk.piley.model.user

enum class PileMode(val value: Int) {
    FREE(0),
    FIFO(1),
    LIFO(2);

    companion object {
        fun fromValue(value: Int) = values().firstOrNull { it.value == value } ?: FREE
    }
}