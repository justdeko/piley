package com.dk.piley.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dk.piley.ui.common.DropDown
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.util.BigSpacer
import com.dk.piley.util.MediumSpacer

/**
 * Base settings item
 *
 * @param modifier generic modifier
 * @param title setting title
 * @param description setting description
 * @param onClick on settings click
 * @param contentEnd content at the end of the settings title and description
 * @param contentBottom content below the settings title and description
 */
@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    onClick: (() -> Unit)? = null,
    contentEnd: (@Composable () -> Unit)? = null,
    contentBottom: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .clickable(enabled = onClick != null, onClick = onClick ?: {})
            .fillMaxWidth()
            .padding(horizontal = LocalDim.current.large, vertical = LocalDim.current.medium),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (contentEnd != null) {
                Row(modifier = Modifier.widthIn(0.dp, 160.dp)) {
                    BigSpacer()
                    contentEnd()
                }
            }
        }
        if (contentBottom != null) {
            MediumSpacer()
            contentBottom()
        }
    }
}

/**
 * Settings item with a switch
 *
 * @param modifier generic modifier
 * @param title setting title
 * @param description setting description
 * @param value switch value (true/false for on/off)
 * @param onValueChange on switch value change
 */
@Composable
fun SwitchSettingsItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
) {
    SettingsItem(
        modifier = modifier,
        title = title,
        description = description,
        contentEnd = { Switch(checked = value, onCheckedChange = onValueChange) })
}

/**
 * Settings item with a dropdown menu
 *
 * @param modifier generic modifier
 * @param title setting title
 * @param description setting description
 * @param optionLabel dropdown menu option label
 * @param selectedValue selected dropdown value
 * @param values dropdown value list
 * @param onValueChange on dropdown value change (item selection)
 */
@Composable
fun DropdownSettingsItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    optionLabel: String,
    selectedValue: String,
    values: List<String>,
    onValueChange: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    SettingsItem(modifier = modifier, title = title, description = description, contentEnd = {
        DropDown(
            value = selectedValue,
            dropdownValues = values,
            expanded = expanded,
            label = optionLabel,
            onExpandedChange = { expanded = !expanded },
            onValueClick = {
                onValueChange(it)
                expanded = false
            },
            onDismiss = { expanded = false }
        )
    })
}

/**
 * Settings item with a slider
 *
 * @param modifier generic modifier
 * @param title setting title
 * @param description setting description
 * @param value slider value
 * @param range slider range as a pair of [min, max] (inclusive)
 * @param steps number of steps within the slider. the values for each step are calculated automatically
 * @param onValueChange on slider value change
 * @receiver
 */
@Composable
fun SliderSettingsItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    value: Int,
    range: Pair<Int, Int>,
    steps: Int,
    onValueChange: (Int) -> Unit
) {
    SettingsItem(modifier = modifier, title = title, description = description, contentBottom = {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Slider(
                modifier = Modifier.weight(1f),
                value = value.toFloat(),
                steps = steps,
                onValueChange = { onValueChange(it.toInt()) },
                valueRange = range.first.toFloat()..range.second.toFloat()
            )
            MediumSpacer()
            Text(
                text = value.toString(),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    })
}
