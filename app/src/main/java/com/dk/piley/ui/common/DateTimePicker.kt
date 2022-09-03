/**
 * Original code from Compose-ToDo
 * https://github.com/wisnukurniawan/Compose-ToDo
 */

package com.dk.piley.ui.common

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.dk.piley.ui.util.utcZoneId
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

private const val DATE_PICKER_TAG = "date_picker"
private const val TIME_PICKER_TAG = "time_picker"

fun Context.showDatePicker(
    selection: LocalDate? = null,
    selectedDate: (LocalDate) -> Unit
) {
    val picker = MaterialDatePicker
        .Builder
        .datePicker()
        .apply {
            if (selection != null) {
                setSelection(selection.atStartOfDay(utcZoneId).toInstant().toEpochMilli())
            }
        }
        .build()
    try {
        val activity = this as AppCompatActivity
        picker.show(activity.supportFragmentManager, DATE_PICKER_TAG)
    } catch (e: Exception) {
        return
    }
    picker.addOnPositiveButtonClickListener {
        selectedDate(
            Instant
                .ofEpochMilli(it)
                .atZone(utcZoneId)
                .toLocalDate()
        )
    }
}

fun Context.showTimePicker(
    time: LocalTime? = null,
    is24hFormat: Boolean = true,
    selectedTime: (LocalTime) -> Unit
) {
    val timeFormat = if (is24hFormat) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
    val picker = MaterialTimePicker
        .Builder()
        .setTimeFormat(timeFormat)
        .apply {
            if (time != null) {
                setHour(time.hour)
                setMinute(time.minute)
            } else {
                setHour(9)
            }
        }
        .build()
    try {
        val activity = this as AppCompatActivity
        picker.show(activity.supportFragmentManager, TIME_PICKER_TAG)
    } catch (e: Exception) {
        return
    }
    picker.addOnPositiveButtonClickListener {
        selectedTime(LocalTime.of(picker.hour, picker.minute))
    }
}
