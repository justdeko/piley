package com.dk.piley.ui.task

import android.Manifest
import android.content.res.Configuration
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomDrawer
import androidx.compose.material.BottomDrawerState
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.R
import com.dk.piley.model.task.RecurringTimeRange
import com.dk.piley.ui.common.DropDown
import com.dk.piley.ui.common.TextWithCheckbox
import com.dk.piley.ui.common.showDatePicker
import com.dk.piley.ui.common.showTimePicker
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.getFrequencyString
import com.dk.piley.util.toRecurringTimeRange
import com.dk.piley.util.toText
import com.dk.piley.util.utcZoneId
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Composable
fun AddReminderDrawer(
    modifier: Modifier = Modifier,
    drawerState: BottomDrawerState,
    initialDate: LocalDateTime? = null,
    isRecurring: Boolean = false,
    recurringTimeRange: RecurringTimeRange = RecurringTimeRange.DAILY,
    recurringFrequency: Int = 1,
    onAddReminder: (ReminderState) -> Unit = {},
    onDeleteReminder: () -> Unit = {},
    permissionState: PermissionState? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    },
    content: @Composable () -> Unit
) {
    BottomDrawer(
        drawerContent = {
            AddReminderContent(
                modifier = modifier,
                drawerState = drawerState,
                onAddReminder = onAddReminder,
                onDeleteReminder = onDeleteReminder,
                initialDateTime = initialDate,
                isRecurring = isRecurring,
                recurringTimeRange = recurringTimeRange,
                recurringFrequency = recurringFrequency,
                permissionState = permissionState
            )
        },
        gesturesEnabled = !drawerState.isClosed,
        drawerState = drawerState,
        drawerBackgroundColor = MaterialTheme.colorScheme.surface,
        drawerShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Composable
fun AddReminderContent(
    modifier: Modifier = Modifier,
    drawerState: BottomDrawerState,
    onAddReminder: (ReminderState) -> Unit,
    onDeleteReminder: () -> Unit = {},
    initialDateTime: LocalDateTime? = null,
    isRecurring: Boolean = false,
    recurringTimeRange: RecurringTimeRange = RecurringTimeRange.DAILY,
    recurringFrequency: Int = 1,
    permissionState: PermissionState? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    },
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var localDate: LocalDate? by remember { mutableStateOf(null) }
    var localTime: LocalTime? by remember { mutableStateOf(null) }
    var expandedTimeRange by remember { mutableStateOf(false) }
    var expandedFrequency by remember { mutableStateOf(false) }
    val timeRanges = stringArrayResource(R.array.time_range).toList()
    var recurring by remember(isRecurring) { (mutableStateOf(isRecurring)) }
    var timeRange by remember(recurringTimeRange) { (mutableStateOf(recurringTimeRange)) }
    var frequency by remember(recurringFrequency) { (mutableIntStateOf(recurringFrequency)) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        Text(
            text = if (initialDateTime != null) "Edit reminder" else "Add reminder",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Start
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                localDate?.toString() ?: (initialDateTime?.toLocalDate()?.toString()
                    ?: "Pick a date")
            )
            IconButton(onClick = {
                context.showDatePicker(localDate ?: initialDateTime?.toLocalDate()) {
                    localDate = it
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Event,
                    "set the date for a reminder",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                localTime?.toString() ?: (initialDateTime?.toLocalTime()?.withNano(0)
                    ?.toString()
                    ?: "Pick a time")
            )
            IconButton(onClick = {
                context.showTimePicker(localTime ?: initialDateTime?.toLocalTime()) {
                    localTime = it
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    "set the date for a reminder",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
        TextWithCheckbox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            description = "Recurring",
            checked = recurring
        ) { recurring = it }
        AnimatedVisibility(recurring) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DropDown(
                        modifier = Modifier.weight(1f),
                        value = timeRange.toText(),
                        dropdownValues = timeRanges,
                        expanded = expandedTimeRange,
                        label = "Time Range",
                        onExpandedChange = { expandedTimeRange = !expandedTimeRange },
                        onValueClick = {
                            expandedTimeRange = false
                            timeRange = it.toRecurringTimeRange(context)
                        },
                        onDismiss = { expandedTimeRange = false }
                    )
                    Spacer(Modifier.size(16.dp))
                    DropDown(
                        modifier = Modifier.weight(1f),
                        value = frequency.toString(),
                        dropdownValues = listOf(1, 2, 3, 4, 5).map { it.toString() },
                        expanded = expandedFrequency,
                        label = "Frequency",
                        onExpandedChange = { expandedFrequency = !expandedFrequency },
                        onValueClick = {
                            expandedFrequency = false
                            frequency = Integer.parseInt(it)
                        },
                        onDismiss = { expandedFrequency = false }
                    )
                }
                Text(
                    modifier = Modifier
                        .align(CenterHorizontally)
                        .padding(top = 8.dp),
                    text = getFrequencyString(timeRange, frequency)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                enabled = (((localTime != null && localDate != null) || (initialDateTime != null))),
                onClick = {
                    // if permission denied, do nothing and show toast
                    if (permissionState != null && !permissionState.status.isGranted) {
                        Toast.makeText(
                            context,
                            "This reminder won't show up because you haven't enabled notifications.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@Button
                    }
                    if (localTime != null && localDate != null) {
                        localTime?.atDate(localDate)?.let {
                            onAddReminder(
                                ReminderState(
                                    reminder = it,
                                    recurring = recurring,
                                    recurringTimeRange = timeRange,
                                    recurringFrequency = frequency
                                )
                            )
                        }
                    } else if (initialDateTime != null) {
                        // case of an existing reminder getting updated
                        // where only one or no fields were touched
                        val time = localTime ?: initialDateTime.toLocalTime()
                        val date = localDate ?: initialDateTime.toLocalDate()
                        onAddReminder(
                            ReminderState(
                                reminder = time.atDate(date),
                                recurring = recurring,
                                recurringTimeRange = timeRange,
                                recurringFrequency = frequency
                            )
                        )
                    }
                    coroutineScope.launch {
                        drawerState.close()
                    }
                }
            ) {
                Text(if (initialDateTime == null) "Set Reminder" else "Update")
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
                    Text("Delete")
                }
            }
        }
    }
}

data class ReminderState(
    val reminder: LocalDateTime,
    val recurring: Boolean,
    val recurringTimeRange: RecurringTimeRange,
    val recurringFrequency: Int,
)

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun AddReminderDrawerPreview() {
    AndroidThreeTen.init(LocalContext.current)
    PileyTheme(useDarkTheme = true) {
        Surface {
            val drawerState = BottomDrawerState(BottomDrawerValue.Open)
            AddReminderDrawer(
                content = {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text("some text here")
                    }
                }, modifier = Modifier, drawerState = drawerState, permissionState = null
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Preview(showBackground = true)
@Composable
fun EditReminderDrawerPreview() {
    AndroidThreeTen.init(LocalContext.current)
    PileyTheme(useDarkTheme = true) {
        Surface {
            val initialDateTime = LocalDateTime.now(utcZoneId)
            val drawerState = BottomDrawerState(BottomDrawerValue.Open)
            AddReminderDrawer(
                initialDate = initialDateTime, content = {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text("some text here")
                    }
                }, modifier = Modifier, drawerState = drawerState, permissionState = null
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Preview(showBackground = true)
@Composable
fun EditReminderDrawerRecurringPreview() {
    AndroidThreeTen.init(LocalContext.current)
    PileyTheme(useDarkTheme = true) {
        Surface {
            val initialDateTime = LocalDateTime.now(utcZoneId)
            val drawerState = BottomDrawerState(BottomDrawerValue.Open)
            AddReminderDrawer(
                initialDate = initialDateTime,
                content = {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text("some text here")
                    }
                },
                isRecurring = true,
                modifier = Modifier,
                drawerState = drawerState,
                permissionState = null
            )
        }
    }
}