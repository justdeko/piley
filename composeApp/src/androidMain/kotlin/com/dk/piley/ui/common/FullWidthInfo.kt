package com.dk.piley.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import com.dk.piley.util.MediumSpacer

/**
 * Full width info section with label and value
 *
 * @param modifier default modifier
 * @param label info label
 * @param value info value
 */
@Composable
fun FullWidthInfo(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = LocalDim.current.large),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = label,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.labelLarge.copy(lineBreak = LineBreak.Heading)
        )
        MediumSpacer()
        Text(
            textAlign = TextAlign.End,
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}