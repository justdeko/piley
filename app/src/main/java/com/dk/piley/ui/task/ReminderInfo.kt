package com.dk.piley.ui.task

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.model.task.RecurringTimeRange
import com.dk.piley.ui.theme.PileyTheme

@Composable
fun ReminderInfo(
    modifier: Modifier = Modifier,
    reminderDateTimeText: String? = null,
    isRecurring: Boolean = false,
    recurringTimeRange: RecurringTimeRange = RecurringTimeRange.DAILY,
    recurringFrequency: Int = 1,
    onAddReminder: () -> Unit = {},
) {
    val reminderSet = reminderDateTimeText != null
    Column(modifier = modifier) {
        Text(
            text = "Reminder",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 16.dp),
            textAlign = TextAlign.Start
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(16.dp)
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = reminderDateTimeText ?: "No reminder set",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(
                onClick = onAddReminder,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = if (reminderSet) Icons.Default.Edit else Icons.Default.AddAlert,
                    "set or delete a task reminder",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .scale(1.1F),
                    tint = if (reminderSet) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview
@Composable
fun ReminderInfoPreview() {
    PileyTheme(useDarkTheme = true) {
        ReminderInfo(Modifier.fillMaxWidth())
    }
}

@Preview
@Composable
fun ReminderInfoSetPreview() {
    PileyTheme(useDarkTheme = true) {
        ReminderInfo(Modifier.fillMaxWidth(), "08.02.2020 13:34")
    }
}