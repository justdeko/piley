package com.dk.piley.ui.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.R
import com.dk.piley.model.task.RecurringTimeRange
import com.dk.piley.model.task.toRecurringTimeRange
import com.dk.piley.model.task.toText
import com.dk.piley.ui.common.DropDown
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.common.TextWithCheckbox
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.BigSpacer
import com.dk.piley.util.MediumSpacer
import com.dk.piley.util.getFrequencyString

@Composable
fun RecurringReminderSection(
    modifier: Modifier = Modifier,
    selectedTimeRange: RecurringTimeRange,
    selectedFrequency: Int,
    onSelectTimeRange: (RecurringTimeRange) -> Unit = {},
    onSelectFrequency: (Int) -> Unit = {},
    useNowAsReminderTime: Boolean = false,
    onUseNowAsReminderTimeChange: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val timeRanges = stringArrayResource(R.array.time_range).toList()
    var expandedTimeRange by remember { mutableStateOf(false) }
    var expandedFrequency by remember { mutableStateOf(false) }

    Column(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DropDown(
                modifier = Modifier.weight(1f),
                value = selectedTimeRange.toText(),
                dropdownValues = timeRanges,
                expanded = expandedTimeRange,
                label = stringResource(R.string.reminder_time_range_label),
                onExpandedChange = { expandedTimeRange = !expandedTimeRange },
                onValueClick = {
                    expandedTimeRange = false
                    onSelectTimeRange(it.toRecurringTimeRange(context))
                },
                onDismiss = { expandedTimeRange = false }
            )
            BigSpacer()
            DropDown(
                modifier = Modifier.weight(1f),
                value = selectedFrequency.toString(),
                dropdownValues = listOf(1, 2, 3, 4, 5).map { it.toString() },
                expanded = expandedFrequency,
                label = stringResource(R.string.reminder_frequency_label),
                onExpandedChange = { expandedFrequency = !expandedFrequency },
                onValueClick = {
                    expandedFrequency = false
                    onSelectFrequency(Integer.parseInt(it))
                },
                onDismiss = { expandedFrequency = false }
            )
        }
        MediumSpacer()
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.onBackground,
            text = getFrequencyString(selectedTimeRange, selectedFrequency)
        )
        MediumSpacer()
        TextWithCheckbox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LocalDim.current.medium),
            description = stringResource(R.string.reminder_use_now_description),
            checked = useNowAsReminderTime,
            onChecked = onUseNowAsReminderTimeChange
        )
    }
}

@Preview
@Composable
private fun PreviewRecurringReminderSection() {
    PileyTheme(useDarkTheme = true) {
        RecurringReminderSection(
            selectedTimeRange = RecurringTimeRange.WEEKLY,
            selectedFrequency = 2
        )
    }
}