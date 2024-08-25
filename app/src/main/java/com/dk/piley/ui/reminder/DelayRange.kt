package com.dk.piley.ui.reminder

enum class DelayRange {
    Minute,
    Hour,
    Day,
    Week,
    Month,
}

val delaySelectionMap = mutableMapOf(
    DelayRange.Minute to listOf(10, 15, 30, 45),
    DelayRange.Hour to listOf(1, 2, 6, 12),
    DelayRange.Day to listOf(1, 2, 3, 4, 5),
    DelayRange.Week to listOf(1, 2, 3, 4),
    DelayRange.Month to listOf(1, 2, 3)
)
