package com.dk.piley.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
        modifier = modifier.fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = onExpandedChange,
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            readOnly = true,
            value = value,
            shape = MaterialTheme.shapes.large,
            onValueChange = {},
            label = label?.let { { Text(label) } },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
        )
        ExposedDropdownMenu(
            modifier = Modifier.exposedDropdownSize(),
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
