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
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.ui.theme.PileyTheme

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

@Preview
@Composable
fun ButtonRowPreview() {
    PileyTheme(useDarkTheme = true) {
        TwoButtonRow(
            onRightClick = {},
            onLeftClick = {},
            rightText = "Confirm",
            leftText = "Cancel"
        )
    }
}