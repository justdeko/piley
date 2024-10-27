package com.dk.piley.model.user

/**
 * Pile mode representing how tasks can be completed within the pile.
 * FREE: tasks can be completed from any place of the pile
 * FIFO (first in first out): tasks at the bottom of the pile (entered first) have also to be completed first
 * LIFO (last in first out): tasks at the top of the pile (entered last) have to be completed first
 *
 * @property value
 * @constructor Create empty Pile mode
 */
enum class PileMode(val value: Int) {
    FREE(0),
    FIFO(1),
    LIFO(2);

    companion object {
        fun fromValue(value: Int) = entries.firstOrNull { it.value == value } ?: FREE
    }
}