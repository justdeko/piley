package com.dk.piley.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


/**
 * Text with checkbox
 *
 * @param modifier default modifier
 * @param description checkbox description
 * @param checked whether checkbox is checked
 * @param onChecked on checkbox checked/unchecked
 */
@Composable
fun TextWithCheckbox(
    modifier: Modifier = Modifier,
    description: String,
    checked: Boolean,
    onChecked: ((Boolean) -> Unit)? = null
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Checkbox(
            checked = checked,
            onCheckedChange = onChecked
        )
    }
}
