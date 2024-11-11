package com.dk.piley.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Row with two buttons
 *
 * @param modifier default modifier
 * @param onRightClick on right button click
 * @param onLeftClick on left button click
 * @param rightText right button text
 * @param leftText left button text
 * @param rightEnabled whether right button is enabled
 * @param arrangement button horizontal arrangement
 * @param rightColors right button colors
 * @param leftColors left button colors
 */
@Composable
fun TwoButtonRow(
    modifier: Modifier = Modifier,
    onRightClick: () -> Unit,
    onLeftClick: () -> Unit,
    rightText: String,
    leftText: String,
    rightEnabled: Boolean = true,
    arrangement: Arrangement.Horizontal = Arrangement.SpaceAround,
    rightColors: ButtonColors = ButtonDefaults.buttonColors(),
    leftColors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    )
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = arrangement
    ) {
        Button(
            onClick = onLeftClick,
            colors = leftColors,
        ) {
            Text(leftText)
        }
        Button(
            onClick = onRightClick,
            enabled = rightEnabled,
            colors = rightColors
        ) {
            Text(rightText)
        }
    }
}
