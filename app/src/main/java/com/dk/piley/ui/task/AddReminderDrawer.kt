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
import com.dk.piley.ui.util.toDate
import com.dk.piley.ui.util.toLocalDateTime
import com.dk.piley.ui.util.utcZoneId
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddReminderDrawer(
    content: @Composable () -> Unit,
    modifier: Modifier,
    drawerState: BottomDrawerState,
    onAddReminder: (Date) -> Unit = {},
    initialDate: Date? = null
) {
    BottomDrawer(
        drawerContent = {
            AddReminderContent(
                modifier,
                drawerState,
                onAddReminder,
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
    onAddReminder: (Date) -> Unit,
    initialDate: Date? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val initialDateTime = initialDate?.toLocalDateTime()
    var dateText by remember { mutableStateOf("Pick a date") }
    var timeText by remember { mutableStateOf("Pick a time") }
    var localDate by remember { mutableStateOf(LocalDate.now(utcZoneId)) }
    var localTime by remember { mutableStateOf(LocalTime.now(utcZoneId)) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        Text(
            text = "Add reminder",
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
            Text(dateText)
            IconButton(onClick = {
                context.showDatePicker(initialDateTime?.toLocalDate()) {
                    dateText = it.toString()
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
            Text(timeText)
            IconButton(onClick = {
                context.showTimePicker(initialDateTime?.toLocalTime()) {
                    timeText = it.toString()
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
        Button(
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally),
            onClick = {
                val dateTime = localTime.atDate(localDate)
                onAddReminder(dateTime.toDate())
                coroutineScope.launch {
                    drawerState.close()
                }
            }
        ) {
            Text("Set Reminder")
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