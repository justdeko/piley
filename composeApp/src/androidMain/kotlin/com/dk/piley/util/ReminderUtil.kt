package com.dk.piley.util

import androidx.compose.runtime.Composable
import com.dk.piley.model.task.RecurringTimeRange
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.plural_s
import piley.composeapp.generated.resources.reminder_repeat_frequency_value
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
    val pluralS = pluralStringResource(Res.plurals.plural_s, recurringFrequency)
    val frequency = if (recurringFrequency == 1) "" else "$recurringFrequency "
    return stringResource(
        Res.string.reminder_repeat_frequency_value,
        "$frequency${recurringTimeRange.toText()}$pluralS"
    )
}
