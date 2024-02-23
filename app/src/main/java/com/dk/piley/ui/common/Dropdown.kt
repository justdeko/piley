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
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.ui.theme.PileyTheme

/**
 * Generic drop down element
 *
 * @param modifier default modifier
 * @param value current drop down value
 * @param dropdownValues list of dropdown values
 * @param expanded whether dropdown is expanded
 * @param label dropdown label
 * @param onExpandedChange on dropdown collapse/expand
 * @param onValueClick on selecting a value from the dropdown
 * @param onIndexClick on selecting a value by its index from the dropdown
 * @param onDismiss on dropdown dismiss
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDown(
    modifier: Modifier = Modifier,
    value: String,
    dropdownValues: List<String>,
    expanded: Boolean = false,
    label: String?,
    onExpandedChange: (Boolean) -> Unit = {},
    onValueClick: (String) -> Unit = {},
    onIndexClick: (Int) -> Unit = {},
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
                label = label?.let { { Text(label) } },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
            )
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismiss,
        ) {
            dropdownValues.forEachIndexed { index, selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onIndexClick(index)
                        onValueClick(selectionOption)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewDropdown() {
    PileyTheme(useDarkTheme = true) {
        DropDown(value = "a", dropdownValues = listOf("a", "b", "c"), label = "select a letter")
    }
}