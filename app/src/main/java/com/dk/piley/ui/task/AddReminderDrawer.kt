package com.dk.piley.ui.task

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomDrawer
import androidx.compose.material.BottomDrawerState
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.ui.common.showDatePicker
import com.dk.piley.ui.common.showTimePicker
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.ui.util.utcZoneId
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddReminderDrawer(
    modifier: Modifier = Modifier,
    drawerState: BottomDrawerState,
    initialDate: LocalDateTime? = null,
    onAddReminder: (LocalDateTime) -> Unit = {},
    onDeleteReminder: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    BottomDrawer(
        drawerContent = {
            AddReminderContent(
                modifier,
                drawerState,
                onAddReminder,
                onDeleteReminder,
                initialDate
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddReminderContent(
    modifier: Modifier = Modifier,
    drawerState: BottomDrawerState,
    onAddReminder: (LocalDateTime) -> Unit,
    onDeleteReminder: () -> Unit = {},
    initialDateTime: LocalDateTime? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var localDate: LocalDate? by remember { mutableStateOf(null) }
    var localTime: LocalTime? by remember { mutableStateOf(null) }
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
                    ?: "Pick a time")
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
                localTime?.toString() ?: (initialDateTime?.toLocalTime()?.toString()
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                enabled = (((localTime != null && localDate != null) || (initialDateTime != null))),
                onClick = {
                    if (localTime != null && localDate != null) {
                        localTime?.atDate(localDate)?.let {
                            onAddReminder(it)
                        }
                    } else if (initialDateTime != null) {
                        // case of an existing reminder getting updated
                        // where only one or no fields were touched
                        val time = localTime ?: initialDateTime.toLocalTime()
                        val date = localDate ?: initialDateTime.toLocalDate()
                        onAddReminder(time.atDate(date))
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

@OptIn(ExperimentalMaterialApi::class)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun AddReminderDrawerPreview() {
    PileyTheme(useDarkTheme = true) {
        Surface {
            val drawerState = BottomDrawerState(BottomDrawerValue.Open)
            AddReminderDrawer(content = {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text("some text here")
                }
            }, modifier = Modifier, drawerState = drawerState)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun EditReminderDrawerPreview() {
    PileyTheme(useDarkTheme = true) {
        Surface {
            val initialDateTime = LocalDateTime.now(utcZoneId)
            val drawerState = BottomDrawerState(BottomDrawerValue.Open)
            AddReminderDrawer(initialDate = initialDateTime, content = {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text("some text here")
                }
            }, modifier = Modifier, drawerState = drawerState)
        }
    }
}