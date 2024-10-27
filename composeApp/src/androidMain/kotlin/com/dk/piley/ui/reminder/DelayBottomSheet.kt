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
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.reminder.DelayRange
import com.dk.piley.reminder.calculateDelayDuration
import com.dk.piley.ui.common.TwoButtonRow
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.defaultPadding
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.cancel_delay_button
import piley.composeapp.generated.resources.confirm_delay_button
import piley.composeapp.generated.resources.delay_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DelayBottomSheet(
    defaultDelayRange: DelayRange = DelayRange.Minute,
    defaultDelayIndex: Int = 0,
    sheetState: SheetState,
    onDelay: (Long) -> Unit = {}
) {
    var delayTime by remember {
        mutableLongStateOf(
            calculateDelayDuration(
                delayRange = defaultDelayRange,
                delayDurationIndex = defaultDelayIndex
            )
        )
    }
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
                text = stringResource(Res.string.delay_title),
                style = MaterialTheme.typography.headlineMedium
            )
            DelaySelection(
                defaultRangeIndex = defaultDelayRange.ordinal,
                defaultDurationIndex = defaultDelayIndex,
                onSetDelay = { delayTime = it }
            )
            TwoButtonRow(
                leftText = stringResource(Res.string.cancel_delay_button),
                rightText = stringResource(Res.string.confirm_delay_button),
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