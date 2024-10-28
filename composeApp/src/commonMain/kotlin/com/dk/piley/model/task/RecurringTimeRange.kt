package com.dk.piley.model.task

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.getStringArray
import org.jetbrains.compose.resources.stringArrayResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.time_range

/**
 * Represents a time range for recurring tasks
 *
 */
enum class RecurringTimeRange {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

/**
 * Convert recurring time range enum to a string
 *
 * @return string representing the time range, e.g. "Weekly" for WEEKLY
 */
@Composable
fun RecurringTimeRange.toText(): String {
    val timeRanges = stringArrayResource(Res.array.time_range)
    return when (this) {
        RecurringTimeRange.DAILY -> timeRanges[0]
        RecurringTimeRange.WEEKLY -> timeRanges[1]
        RecurringTimeRange.MONTHLY -> timeRanges[2]
        RecurringTimeRange.YEARLY -> timeRanges[3]
    }
}

/**
 * Convert time range string to recurring time range
 *
 * @return time range enum representing corresponding string
 * e.g. WEEKLY for "Weekly"
 */
suspend fun String.toRecurringTimeRange(): RecurringTimeRange {
    val timeRanges = getStringArray(Res.array.time_range)
    return when (this) {
        timeRanges[0] -> RecurringTimeRange.DAILY
        timeRanges[1] -> RecurringTimeRange.WEEKLY
        timeRanges[2] -> RecurringTimeRange.MONTHLY
        timeRanges[3] -> RecurringTimeRange.YEARLY
        else -> RecurringTimeRange.DAILY
    }
}
