package com.dk.piley.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.model.user.PileMode
import com.dk.piley.ui.theme.PileyTheme

@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    contentEnd: (@Composable () -> Unit)? = null,
    contentBottom: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (contentEnd != null) {
                Spacer(modifier = Modifier.width(16.dp))
                contentEnd()
            }
        }
        if (contentBottom != null) {
            Spacer(modifier = Modifier.height(8.dp))
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

@OptIn(ExperimentalMaterial3Api::class)
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
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            TextField(
                modifier = Modifier.menuAnchor(),
                readOnly = true,
                value = selectedValue,
                onValueChange = {},
                label = { Text(optionLabel) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                values.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            onValueChange(selectionOption)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
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