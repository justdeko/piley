package com.dk.piley.ui.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import com.dk.piley.model.task.RecurringTimeRange
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.common.TextWithCheckbox
import com.dk.piley.util.MediumSpacer
import com.dk.piley.util.getFrequencyString
import com.dk.piley.util.roundedOutline
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.no_reminder_set_placeholder
import piley.composeapp.generated.resources.reminder_info_title
import piley.composeapp.generated.resources.reminder_recurring_label

/**
 * Reminder info section
 *
 * @param modifier generic modifier
 * @param reminderDateTimeText formatted date and time text of reminder
 * @param isRecurring whether reminder is recurring
 * @param recurringTimeRange time range of recurring reminder
 * @param recurringFrequency frequency of recurring reminder
 * @param onAddReminder on add reminder click
 * @param addReminderButtonEnabled whether add reminder button is enabled
 */
@Composable
fun ReminderInfo(
    modifier: Modifier = Modifier,
    reminderDateTimeText: String? = null,
    isRecurring: Boolean = false,
    recurringTimeRange: RecurringTimeRange = RecurringTimeRange.DAILY,
    recurringFrequency: Int = 1,
    onAddReminder: () -> Unit = {},
    addReminderButtonEnabled: Boolean = true
) {
    val reminderSet = reminderDateTimeText != null
    val dim = LocalDim.current
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.reminder_info_title),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = dim.medium),
            textAlign = TextAlign.Start
        )
        Column(
            Modifier
                .padding(dim.medium)
                .roundedOutline()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reminderDateTimeText
                        ?: stringResource(Res.string.no_reminder_set_placeholder),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = dim.large),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(
                    onClick = onAddReminder,
                    enabled = addReminderButtonEnabled,
                    modifier = Modifier.padding(vertical = dim.medium, horizontal = dim.small)
                ) {
                    Icon(
                        imageVector = if (reminderSet) Icons.Default.Edit else Icons.Default.AddAlert,
                        "set or delete a task reminder",
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        tint = if (reminderSet) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                    )
                }
            }
            TextWithCheckbox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dim.large, vertical = dim.medium),
                description = stringResource(Res.string.reminder_recurring_label),
                checked = isRecurring
            )
            if (isRecurring) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dim.large, vertical = dim.small),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = getFrequencyString(recurringTimeRange, recurringFrequency),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
            MediumSpacer()
        }
    }
}
