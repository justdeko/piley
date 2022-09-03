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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddReminderDrawer(
    content: @Composable () -> Unit,
    modifier: Modifier,
    drawerState: BottomDrawerState
) {
    BottomDrawer(
        drawerContent = { AddReminderContent(modifier, drawerState) },
        gesturesEnabled = !drawerState.isClosed,
        drawerState = drawerState,
        drawerBackgroundColor = MaterialTheme.colorScheme.surface,
        drawerShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddReminderContent(modifier: Modifier = Modifier, drawerState: BottomDrawerState) {
    val context = LocalContext.current
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
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
            Text("Pick a date: $date")
            IconButton(onClick = {
                context.showDatePicker { date = it.toString() }
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
            Text("Pick a time: $time")
            IconButton(onClick = {
                context.showTimePicker { time = it.toString() }
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
            onClick = { /*TODO*/ }
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