package com.dk.piley.ui.reminder

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.R
import com.dk.piley.ui.common.TwoButtonRow
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.defaultPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DelayBottomSheet(
    defaultDelayRange: DelayRange = DelayRange.Minute,
    defaultDelayIndex: Int = 0,
    sheetState: SheetState,
    onDelay: (Long) -> Unit = {}
) {
    var delayTime by remember { mutableLongStateOf(0) }
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
            Text(
                text = stringResource(R.string.delay_title),
                style = MaterialTheme.typography.headlineMedium
            )
            DelaySelection(
                defaultRangeIndex = defaultDelayRange.ordinal,
                defaultDurationIndex = defaultDelayIndex,
                onSetDelay = { delayTime = it }
            )
            TwoButtonRow(
                leftText = stringResource(R.string.cancel_delay_button),
                rightText = stringResource(R.string.confirm_delay_button),
                onLeftClick = { onDelay(0) },
                onRightClick = { onDelay(delayTime) }
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