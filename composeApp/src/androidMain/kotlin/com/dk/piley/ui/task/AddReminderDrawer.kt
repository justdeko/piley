@file:OptIn(ExperimentalMaterial3Api::class)

package com.dk.piley.ui.task

import android.content.res.Configuration
import android.text.format.DateFormat
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.model.task.RecurringTimeRange
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.common.ReminderDatePicker
import com.dk.piley.ui.common.ReminderTimePicker
import com.dk.piley.ui.common.TextWithCheckbox
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.BigSpacer
import com.dk.piley.util.MediumSpacer
import com.dk.piley.util.dateString
import com.dk.piley.util.defaultPadding
import com.dk.piley.util.timeString
import com.dk.piley.util.toLocalDateTime
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.add_reminder_title
import piley.composeapp.generated.resources.date_selection_placeholder
import piley.composeapp.generated.resources.delete_reminder_button
import piley.composeapp.generated.resources.edit_reminder_title
import piley.composeapp.generated.resources.no_notification_permission_reminder_warning
import piley.composeapp.generated.resources.reminder_recurring_label
import piley.composeapp.generated.resources.set_reminder_button
import piley.composeapp.generated.resources.time_selection_placeholder
import piley.composeapp.generated.resources.update_reminder_button

/**
 * Bottom sheet with options to add a reminder.
 *
 * @param modifier generic modifier
 * @param sheetState bottom sheet state
 * @param initialDate initial reminder date time
 * @param isRecurring whether reminder is recurring
 * @param recurringTimeRange time range for recurring reminders
 * @param recurringFrequency frequency for recurring reminders
 * @param useNowAsReminderDate whether to use current time as reminder date
 * @param onAddReminder on add or update reminder
 * @param onDeleteReminder on delete reminder
 * @param notificationPermissionGranted whether the notification permission was granted
 */
@Composable
fun AddReminderDrawer(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    initialDate: LocalDateTime? = null,
    isRecurring: Boolean = false,
    recurringTimeRange: RecurringTimeRange = RecurringTimeRange.DAILY,
    recurringFrequency: Int = 1,
    useNowAsReminderDate: Boolean = false,
    onAddReminder: (ReminderState) -> Unit = {},
    onDeleteReminder: () -> Unit = {},
    onDismiss: () -> Unit = {},
    notificationPermissionGranted: Boolean = false,
) {
    ModalBottomSheet(
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(
            topStart = LocalDim.current.veryLarge,
            topEnd = LocalDim.current.veryLarge
        ),
        onDismissRequest = onDismiss
    ) {
        AddReminderContent(
            modifier = modifier,
            onAddReminder = onAddReminder,
            onDeleteReminder = onDeleteReminder,
            initialDateTime = initialDate,
            isRecurring = isRecurring,
            recurringTimeRange = recurringTimeRange,
            recurringFrequency = recurringFrequency,
            useNowAsReminderTime = useNowAsReminderDate,
            notificationPermissionGranted = notificationPermissionGranted
        )
    }
}

/**
 * Add/edit reminder content
 *
 * @param modifier generic modifier
 * @param onAddReminder on add or update reminder
 * @param onDeleteReminder on delete reminder
 * @param initialDateTime initial reminder date time
 * @param isRecurring whether reminder is recurring
 * @param recurringTimeRange time range for recurring reminders
 * @param recurringFrequency frequency for recurring reminders
 * @param useNowAsReminderTime whether to use current time as reminder date
 * @param notificationPermissionGranted whether the notification permission was granted
 */
@Composable
fun AddReminderContent(
    modifier: Modifier = Modifier,
    onAddReminder: (ReminderState) -> Unit,
    onDeleteReminder: () -> Unit = {},
    initialDateTime: LocalDateTime? = null,
    isRecurring: Boolean = false,
    recurringTimeRange: RecurringTimeRange = RecurringTimeRange.DAILY,
    recurringFrequency: Int = 1,
    useNowAsReminderTime: Boolean = false,
    notificationPermissionGranted: Boolean = false
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val dim = LocalDim.current
    var localDate: LocalDate? by remember { mutableStateOf(null) }
    var localTime: LocalTime? by remember { mutableStateOf(null) }
    var recurring by remember(isRecurring) { (mutableStateOf(isRecurring)) }
    var timeRange by remember(recurringTimeRange) { (mutableStateOf(recurringTimeRange)) }
    var frequency by remember(recurringFrequency) { (mutableIntStateOf(recurringFrequency)) }
    var datePickerVisible by remember { mutableStateOf(false) }
    var timePickerVisible by remember { mutableStateOf(false) }
    var nowAsReminderTime by remember { mutableStateOf(useNowAsReminderTime) }

    if (datePickerVisible) {
        ReminderDatePicker(
            initialDate = localDate ?: initialDateTime?.date,
            onDismiss = { datePickerVisible = false },
            onConfirm = {
                localDate = it
                datePickerVisible = false
            }
        )
    }

    if (timePickerVisible) {
        ReminderTimePicker(
            initialTime = localTime ?: initialDateTime?.time,
            onDismiss = { timePickerVisible = false },
            is24hFormat = DateFormat.is24HourFormat(context),
            onConfirm = {
                localTime = it
                timePickerVisible = false
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .defaultPadding()
    ) {
        Text(
            modifier = Modifier.align(CenterHorizontally),
            text = if (initialDateTime != null) {
                stringResource(Res.string.edit_reminder_title)
            } else stringResource(
                Res.string.add_reminder_title
            ),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Start
        )
        BigSpacer()
        PickerSection(
            modifier = Modifier.padding(horizontal = dim.large),
            text = localDate?.dateString() ?: (initialDateTime?.date?.dateString()
                ?: stringResource(Res.string.date_selection_placeholder)),
            icon = Icons.Default.Event,
            onIconClick = { datePickerVisible = true },
            iconContentDescription = "set the date for a reminder"
        )
        BigSpacer()
        PickerSection(
            modifier = Modifier.padding(horizontal = dim.large),
            text = localTime?.timeString() ?: (
                    with(initialDateTime?.time) {
                        this?.let {
                            LocalTime(
                                it.hour,
                                this.minute,
                                this.second
                            )
                        }
                    }?.timeString() ?: stringResource(Res.string.time_selection_placeholder)),
            icon = Icons.Default.Schedule,
            onIconClick = { timePickerVisible = true },
            iconContentDescription = "set the time for a reminder"
        )
        MediumSpacer()
        ReminderTimeSuggestions(
            onSelectTimeSuggestion = {
                localDate = it.date
                localTime = it.time
            }
        )
        TextWithCheckbox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dim.large),
            description = stringResource(Res.string.reminder_recurring_label),
            checked = recurring
        ) { recurring = it }
        AnimatedVisibility(recurring) {
            RecurringReminderSection(
                selectedTimeRange = timeRange,
                selectedFrequency = frequency,
                onSelectTimeRange = { timeRange = it },
                onSelectFrequency = { frequency = it },
                useNowAsReminderTime = nowAsReminderTime,
                onUseNowAsReminderTimeChange = { nowAsReminderTime = it }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dim.large),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                enabled = (((localTime != null && localDate != null) || (initialDateTime != null))),
                onClick = {
                    // if permission denied, do nothing and show toast
                    if (!notificationPermissionGranted) {
                        coroutineScope.launch {
                            Toast.makeText(
                                context,
                                getString(Res.string.no_notification_permission_reminder_warning),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        return@Button
                    }
                    if (localTime != null && localDate != null) {
                        localDate?.let { date ->
                            localTime?.let { time ->
                                onAddReminder(
                                    ReminderState(
                                        reminder = LocalDateTime(date, time),
                                        recurring = recurring,
                                        recurringTimeRange = timeRange,
                                        recurringFrequency = frequency,
                                        nowAsReminderTime = nowAsReminderTime
                                    )
                                )
                            }
                        }
                    } else if (initialDateTime != null) {
                        // case of an existing reminder getting updated
                        // where only one or no fields were touched
                        val date = localDate ?: initialDateTime.date
                        val time = localTime ?: initialDateTime.time
                        onAddReminder(
                            ReminderState(
                                reminder = LocalDateTime(date, time),
                                recurring = recurring,
                                recurringTimeRange = timeRange,
                                recurringFrequency = frequency,
                                nowAsReminderTime = nowAsReminderTime
                            )
                        )
                    }
                }
            ) {
                Text(
                    if (initialDateTime == null) {
                        stringResource(Res.string.set_reminder_button)
                    } else stringResource(
                        Res.string.update_reminder_button
                    )
                )
            }
            if (initialDateTime != null) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ), onClick = {
                        // reset date and time pickers
                        localDate = null
                        localTime = null
                        // delete reminder
                        onDeleteReminder()
                    }
                ) {
                    Text(stringResource(Res.string.delete_reminder_button))
                }
            }
        }
    }
}

/**
 * Picker section of reminder, used for date and time picker
 *
 * @param modifier generic modifier
 * @param text section text
 * @param icon section icon
 * @param onIconClick on section icon click
 * @param iconContentDescription icon content description
 */
@Composable
fun PickerSection(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector,
    onIconClick: () -> Unit,
    iconContentDescription: String? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text)
        IconButton(onClick = onIconClick) {
            Icon(
                imageVector = icon,
                iconContentDescription,
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

data class ReminderState(
    val reminder: LocalDateTime,
    val recurring: Boolean,
    val recurringTimeRange: RecurringTimeRange,
    val nowAsReminderTime: Boolean,
    val recurringFrequency: Int,
)

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun AddReminderDrawerPreview() {
    PileyTheme(useDarkTheme = true) {
        Surface {
            val sheetState = rememberStandardBottomSheetState(SheetValue.Expanded)
            AddReminderDrawer(
                sheetState = sheetState,
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun EditReminderDrawerPreview() {
    PileyTheme(useDarkTheme = true) {
        Surface {
            val initialDateTime = Clock.System.now().toLocalDateTime()
            val sheetState = rememberStandardBottomSheetState(SheetValue.Expanded)
            AddReminderDrawer(
                initialDate = initialDateTime,
                sheetState = sheetState,
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun EditReminderDrawerRecurringPreview() {
    PileyTheme(useDarkTheme = true) {
        Surface {
            val initialDateTime = Clock.System.now().toLocalDateTime()
            val sheetState = rememberStandardBottomSheetState(SheetValue.Expanded)
            AddReminderDrawer(
                initialDate = initialDateTime,
                isRecurring = true,
                sheetState = sheetState,
            )
        }
    }
}