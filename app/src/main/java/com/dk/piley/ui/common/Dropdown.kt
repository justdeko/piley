package com.dk.piley.ui.common

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalTextInputService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDown(
    modifier: Modifier = Modifier,
    value: String,
    dropdownValues: List<String>,
    expanded: Boolean = false,
    label: String,
    onExpandedChange: (Boolean) -> Unit = {},
    onValueClick: (String) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = onExpandedChange,
    ) {
        // disable text input provider due to bug where keyboard pops up despite readOnly = true
        CompositionLocalProvider(
            LocalTextInputService provides null
        ) {
            TextField(
                modifier = Modifier.menuAnchor(),
                readOnly = true,
                value = value,
                onValueChange = {},
                label = { Text(label) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
            )
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismiss,
        ) {
            dropdownValues.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = { onValueClick(selectionOption) },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}