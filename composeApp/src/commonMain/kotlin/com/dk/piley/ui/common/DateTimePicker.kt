package com.dk.piley.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dk.piley.util.Platform
import com.dk.piley.util.appPlatform
import com.dk.piley.util.defaultPadding
import com.dk.piley.util.getScreenHeight
import com.dk.piley.util.toLocalDateTime
import com.dk.piley.util.utcZone
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atStartOfDayIn
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.cancel_date_time_picker_button
import piley.composeapp.generated.resources.confirm_date_time_picker_button
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Reminder date picker
 *
 * @param initialDate initial date picker date
 * @param onDismiss on dialog dismiss
 * @param onConfirm on date confirm
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDatePicker(
    initialDate: LocalDate?,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    val datePickerState =
        rememberDatePickerState(initialDate?.atStartOfDayIn(utcZone)?.toEpochMilliseconds())
    val confirmEnabled = remember { derivedStateOf { datePickerState.selectedDateMillis != null } }
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onConfirm(Instant.fromEpochMilliseconds(it).toLocalDateTime().date)
                    }
                },
                enabled = confirmEnabled.value
            ) {
                Text(stringResource(Res.string.confirm_date_time_picker_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel_date_time_picker_button))
            }
        }
    ) { DatePicker(state = datePickerState) }
}

/**
 * Reminder time picker
 *
 * @param initialTime initial time picker time
 * @param is24hFormat whether time picker is in 24h format
 * @param onDismiss on dialog dismiss
 * @param onConfirm on time confirm
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderTimePicker(
    initialTime: LocalTime?,
    is24hFormat: Boolean = true,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit
) {
    val now = Clock.System.now().toLocalDateTime().time
    val state =
        rememberTimePickerState(
            initialHour = initialTime?.hour ?: now.hour,
            initialMinute = initialTime?.minute ?: now.minute,
            is24Hour = is24hFormat
        )
    val showingPicker = remember { mutableStateOf(true) }
    val screenHeight = getScreenHeight()

    ContentAlertDialog(onDismiss = onDismiss) {
        Column(
            Modifier.defaultPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showingPicker.value && screenHeight > 400.dp && appPlatform != Platform.DESKTOP) {
                TimePicker(state = state)
            } else {
                TimeInput(state = state)
            }
            if (screenHeight > 400.dp && appPlatform != Platform.DESKTOP) {
                IconButton(onClick = { showingPicker.value = !showingPicker.value }) {
                    val icon = if (showingPicker.value) {
                        Icons.Outlined.Keyboard
                    } else {
                        Icons.Outlined.Schedule
                    }
                    Icon(
                        icon,
                        contentDescription = if (showingPicker.value) {
                            "Switch to Text Input"
                        } else {
                            "Switch to Touch Input"
                        }
                    )
                }
            }
            TwoButtonRow(
                modifier = Modifier.padding(top = LocalDim.current.medium),
                onRightClick = { onConfirm(LocalTime(state.hour, state.minute)) },
                onLeftClick = onDismiss,
                rightText = stringResource(Res.string.confirm_date_time_picker_button),
                leftText = stringResource(Res.string.cancel_date_time_picker_button)
            )
        }
    }
}
