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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.model.user.PileMode
import com.dk.piley.ui.common.DropDown
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.BigSpacer
import com.dk.piley.util.MediumSpacer

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

@Preview
@Composable
fun SwitchSettingsItemPreview() {
    var on by rememberSaveable { mutableStateOf(false) }
    PileyTheme(useDarkTheme = true) {
        SwitchSettingsItem(
            title = "This is a switch setting",
            description = "With some description",
            value = on,
            onValueChange = { on = it }
        )
    }
}

@Preview
@Composable
fun DropdownSettingsItemPreview() {
    val options = PileMode.values().toList().map { it.name }
    var selectedOption by rememberSaveable { mutableStateOf(PileMode.FREE.name) }
    PileyTheme(useDarkTheme = true) {
        DropdownSettingsItem(
            title = "This is a dropdown setting",
            description = "With some description",
            optionLabel = "Pile Mode",
            selectedValue = selectedOption,
            values = options,
            onValueChange = { selectedOption = it }
        )
    }
}

@Preview
@Composable
fun TextSettingsItemPreview() {
    PileyTheme(useDarkTheme = true) {
        SettingsItem(
            title = "This is a settings item without anything",
            description = "And some description for it"
        )
    }
}

@Preview
@Composable
fun SliderSettingsItemPreview() {
    PileyTheme(useDarkTheme = true) {
        SliderSettingsItem(
            title = "This is a slider setting",
            description = "With some description",
            value = 15,
            range = Pair(15, 45),
            steps = 1,
            onValueChange = {}
        )
    }
}