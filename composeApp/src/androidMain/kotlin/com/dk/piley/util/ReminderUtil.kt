package com.dk.piley.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.dk.piley.R
import com.dk.piley.model.task.RecurringTimeRange
import toText

/**
 * Get frequency string given a recurring reminder time range and frequency
 *
 * @param recurringTimeRange recurring reminder time range
 * @param recurringFrequency recurring reminder frequency
 * @return string representing frequency and time range.
 * E.g. for WEEKLY time range and frequency 2: "Every 2 Weeks"
 */
@Composable
fun getFrequencyString(
    recurringTimeRange: RecurringTimeRange,
    recurringFrequency: Int
): String {
    val pluralS = pluralStringResource(id = R.plurals.plural_s, count = recurringFrequency)
    val frequency = if (recurringFrequency == 1) "" else "$recurringFrequency "
    return stringResource(
        R.string.reminder_repeat_frequency_value,
        "$frequency${recurringTimeRange.toText()}$pluralS"
    )
}
