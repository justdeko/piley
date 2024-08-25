package com.dk.piley.ui.reminder

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.ui.common.TwoButtonRow
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.defaultPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DelayBottomSheet(
    sheetState: SheetState,
    onDelay: (Long) -> Unit = {}
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onDelay(0)
        }
    ) {
        Column(
            modifier = Modifier.defaultPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Delay this task")
            DelaySelection( // TODO set default values
                onSelect = onDelay
            )
            TwoButtonRow(
                leftText = "Don't delay",
                rightText = "Delay",
                onLeftClick = { onDelay(0) },
                onRightClick = { onDelay(0) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun DelayBottomSheetPreview() {
    val sheetState = rememberStandardBottomSheetState(SheetValue.Expanded)
    PileyTheme {
        DelayBottomSheet(sheetState = sheetState)
    }
}